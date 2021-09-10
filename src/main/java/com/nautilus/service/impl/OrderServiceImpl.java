package com.nautilus.service.impl;

import com.nautilus.domain.Customer;
import com.nautilus.domain.Order;
import com.nautilus.domain.OrderItem;
import com.nautilus.domain.dto.OrderDto;
import com.nautilus.repository.OrderRepository;
import com.nautilus.service.CustomerService;
import com.nautilus.service.OrderItemService;
import com.nautilus.service.OrderService;
import com.nautilus.util.Formatter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    final
    OrderItemService orderItemService;

    final
    OrderRepository orderRepository;

    final
    CustomerService customerService;

    @PostConstruct
    public void init() {
        customerService.setOrderService(this);
    }

    public OrderServiceImpl(OrderItemService orderItemService, OrderRepository orderRepository, CustomerService customerService) {
        this.orderItemService = orderItemService;
        this.orderRepository = orderRepository;
        this.customerService = customerService;
    }

    private Order mapToOrder(OrderDto orderDto) {
        return Order.builder()
                .id(orderDto.getId())
                .build();
    }

    @Override
    public Order insert(Order obj) {
        Order savedOrder = orderRepository.insert(obj);
        List<OrderItem> unsavedItems = obj.getItems();
        obj.clearItems();
        unsavedItems.forEach(item -> savedOrder.addItem(orderItemService.insert(item)));
        updateCustomersObligations(savedOrder.getCustomer(), savedOrder);
        customerService.update(savedOrder.getCustomer());
        return savedOrder;
    }

    @Override
    public List<Order> getAll() {
        List<Order> orders = orderRepository.getAll();
        orders.forEach(order -> orderItemService.getAllByOrderId(order.getId()).forEach(order::addItem));
        return orders;
    }

    @Override
    public List<OrderDto> getAllDto(LocalDate date, String city) {
        return orderRepository.getAllDto(date, city);
    }

    @Override
    public Optional<Order> getCustomersLastOrder(Customer customer) {
        Optional<Order> customersLastOrder = orderRepository.getCustomersLastOrder(customer);
        customersLastOrder.ifPresent(order -> orderItemService.getAllByOrderId(order.getId()).forEach(order::addItem));
        return customersLastOrder;
    }

    @Override
    public Order findById(Long id) {
        Order order = orderRepository.findById(id);
        orderItemService.getAllByOrderId(order.getId()).forEach(order::addItem);
        return order;
    }

    @Override
    public Optional<Order> update(Order obj) {
        Order oldOrder = this.findById(obj.getId());
        orderRepository.update(obj);
        List<OrderItem> unsavedItems = obj.getItems();
        obj.clearItems();
        orderItemService.deleteAllByOrderId(obj.getId());
        unsavedItems.forEach(orderItem -> obj.addItem(orderItemService.insert(orderItem)));
        revertCustomersObligations(obj.getCustomer(), oldOrder);
        updateCustomersObligations(obj.getCustomer(), obj);
        customerService.update(obj.getCustomer());
        return Optional.of(obj);
    }

    @Override
    public void deleteAll(Iterable<Order> obj) {
        obj.forEach(orderDto -> {
            Order order = this.findById(orderDto.getId());
            orderRepository.deleteById(order);
            revertCustomersObligations(order.getCustomer(), order);
            customerService.update(order.getCustomer());
        });
    }

    @Override
    public void deleteAllDto(Iterable<OrderDto> obj) {
        List<Order> orders = new ArrayList<>();
        obj.forEach(orderDto -> {
            if (orderDto.getDeliveredBy().equals(Order.DeliveredBy.NONE.toString())) {
                orders.add(mapToOrder(orderDto));
            }
        });
        this.deleteAll(orders);
    }

    private void updateCustomersObligations(Customer customer, Order order) {
        if (!order.getDeliveredBy().equals(Order.DeliveredBy.NONE)) {
            customer.setPackagingSmall(customer.getPackagingSmall() + order.findOrderItemByArticleName(Formatter.WATER_SMALL).map(OrderItem::getQuantity).orElse(0));
            customer.setPackagingLarge(customer.getPackagingLarge() + order.findOrderItemByArticleName(Formatter.WATER_LARGE).map(OrderItem::getQuantity).orElse(0));

        }
        if (!order.getDeliveredBy().equals(Order.DeliveredBy.NONE) && !order.getPayed()) {
            customer.setDebt(customer.getDebt() + ((Double) order.getItems().stream().map(orderItem -> orderItem.getArticlePrice() * (1 + orderItem.getArticleTax() / 100) * orderItem.getQuantity()).mapToDouble(Double::doubleValue).sum()));
        }
    }

    private void revertCustomersObligations(Customer customer, Order order) {
        if (!order.getDeliveredBy().equals(Order.DeliveredBy.NONE)) {
            customer.setPackagingSmall(customer.getPackagingSmall() - order.findOrderItemByArticleName(Formatter.WATER_SMALL).map(OrderItem::getQuantity).orElse(0));
            customer.setPackagingLarge(customer.getPackagingLarge() - order.findOrderItemByArticleName(Formatter.WATER_LARGE).map(OrderItem::getQuantity).orElse(0));
        }
        if (!order.getDeliveredBy().equals(Order.DeliveredBy.NONE) && !order.getPayed()) {
            customer.setDebt(customer.getDebt() - ((Double) order.getItems().stream().map(orderItem -> orderItem.getArticlePrice() * (1 + orderItem.getArticleTax() / 100) * orderItem.getQuantity()).mapToDouble(Double::doubleValue).sum()));
        }
    }

}
