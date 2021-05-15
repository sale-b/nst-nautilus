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
        updateCustomersObligations(obj.getCustomer(), obj);
        updateCustomersPackagingFlags(obj.getCustomer());
        customerService.update(obj.getCustomer());
        return savedOrder;
    }

    @Override
    public List<Order> getAll() {
        List<Order> orders = orderRepository.getAll();
        orders.forEach(order -> orderItemService.getAllByOrderId(order.getId()).forEach(order::addItem));
        return orders;
    }

    @Override
    public List<OrderDto> getAllDto() {
        return orderRepository.getAllDto();
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
        Optional<Order> updatedOrder = orderRepository.update(obj);
        if (updatedOrder.isPresent()) {
            List<OrderItem> unsavedItems = obj.getItems();
            updatedOrder.get().clearItems();
            orderItemService.deleteAllByOrderId(obj.getId());
            unsavedItems.forEach(orderItem -> updatedOrder.get().addItem(orderItemService.insert(orderItem)));
            revertCustomersObligations(updatedOrder.get().getCustomer(), oldOrder);
            updateCustomersObligations(updatedOrder.get().getCustomer(), updatedOrder.get());
            updateCustomersPackagingFlags(obj.getCustomer());
            customerService.update(obj.getCustomer());
        }
        return updatedOrder;
    }

    @Override
    public void deleteAll(Iterable<Order> obj) {
        obj.forEach(orderDto -> {
            Order order = this.findById(orderDto.getId());
            orderRepository.deleteById(order);
            revertCustomersObligations(order.getCustomer(), order);
            updateCustomersPackagingFlags(order.getCustomer());
            customerService.update(order.getCustomer());
        });
    }

    @Override
    public void deleteAllDto(Iterable<OrderDto> obj) {
        List<Order> orders = new ArrayList<>();
        obj.forEach(orderDto -> orders.add(mapToOrder(orderDto)));
        this.deleteAll(orders);
    }

    @Override
    public List<Order> getAllByCustomerIdOrderByDate(Long customerId) {
        return orderRepository.getAllByCustomerIdOrderByDate(customerId);
    }

    private void updateCustomersObligations(Customer customer, Order order) {
        if (!order.getDeliveredBy().equals(Order.DeliveredBy.NONE)) {
            customer.setPackagingSmall(customer.getPackagingSmall() + order.findOrderItemByArticleName(Formatter.WATER_SMALL).map(OrderItem::getQuantity).orElse(0));
            customer.setPackagingLarge(customer.getPackagingLarge() + order.findOrderItemByArticleName(Formatter.WATER_LARGE).map(OrderItem::getQuantity).orElse(0));

        }
        if (!order.getDeliveredBy().equals(Order.DeliveredBy.NONE) && !order.getPayed()) {
            customer.setDebt(customer.getDebt() + ((Double) order.getItems().stream().map(orderItem -> orderItem.getArticlePrice() * orderItem.getQuantity()).mapToDouble(Double::doubleValue).sum()));
        }
    }

    private void revertCustomersObligations(Customer customer, Order order) {
        if (!order.getDeliveredBy().equals(Order.DeliveredBy.NONE)) {
            customer.setPackagingSmall(customer.getPackagingSmall() - order.findOrderItemByArticleName(Formatter.WATER_SMALL).map(OrderItem::getQuantity).orElse(0));
            customer.setPackagingLarge(customer.getPackagingLarge() - order.findOrderItemByArticleName(Formatter.WATER_LARGE).map(OrderItem::getQuantity).orElse(0));
        }
        if (!order.getDeliveredBy().equals(Order.DeliveredBy.NONE) && !order.getPayed()) {
            customer.setDebt(customer.getDebt() - ((Double) order.getItems().stream().map(orderItem -> orderItem.getArticlePrice() * orderItem.getQuantity()).mapToDouble(Double::doubleValue).sum()));
        }
    }

    private void updateCustomersPackagingFlags(Customer customer) {
        Optional<Order> lastOrder = orderRepository.getCustomersLastOrder(customer.getId());
        if (lastOrder.isPresent()) {
            customer.setBacklogPackagingSmall(customer.getPackagingSmall() > lastOrder.get().getPackagingDebtSmall());
            customer.setBacklogPackagingLarge(customer.getPackagingLarge() > lastOrder.get().getPackagingDebtLarge());
        }
    }

}
