package com.nautilus.service;

import com.nautilus.domain.Customer;
import com.nautilus.domain.Order;
import com.nautilus.domain.OrderItem;
import com.nautilus.domain.dto.OrderDto;
import com.nautilus.repository.OrderRepository;
import com.nautilus.service.impl.OrderServiceImpl;
import config.BaseTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest extends BaseTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    OrderItemService orderItemService;

    @Mock
    CustomerService customerService;

    @InjectMocks
    OrderServiceImpl orderService;

    @Test
    void insertTest() {
        Customer customer = setupCustomer();
        assertEquals(Integer.valueOf(0), customer.getPackagingLarge());
        assertEquals(Double.valueOf(0.0), customer.getDebt());

        Order order = setupOrder(customer);

        when(orderRepository.insert(order)).thenReturn(order);
        when(customerService.update(any(Customer.class))).thenAnswer(invocation -> Optional.of(invocation.getArguments()[0]));
        when(orderItemService.insert(order.getItems().get(0))).thenReturn(order.getItems().get(0));

        orderService.insert(order);

        Mockito.verify(orderRepository).insert(order);
        Mockito.verify(customerService).update(customer);
        assertEquals(Integer.valueOf(2), customer.getPackagingLarge());
        assertEquals(Double.valueOf(480.0), customer.getDebt());
    }

    @Test
    void getAllTest() {
        Order order = setupOrder(setupCustomer());
        when(orderRepository.getAll()).thenReturn(Collections.singletonList(order));

        orderService.getAll();

        Mockito.verify(orderRepository).getAll();
        Mockito.verify(orderItemService).getAllByOrderId(any(Long.class));
    }

    @Test
    void getAllDtoTest() {
        orderService.getAllDto(LocalDate.now(), "");

        Mockito.verify(orderRepository).getAllDto(any(LocalDate.class), any(String.class));
    }

    @Test
    void findByIdTest() {
        Order order = setupOrder(setupCustomer());
        when(orderRepository.findById(any(Long.class))).thenReturn(order);
        when(orderItemService.getAllByOrderId(any(Long.class))).thenReturn(order.getItems());

        orderService.findById(order.getId());

        Mockito.verify(orderRepository).findById(any(Long.class));
        Mockito.verify(orderItemService).getAllByOrderId(any(Long.class));
    }

    @Test
    void updateTest() {
        Customer customer = setupCustomer();
        customer.setPackagingLarge(1);
        customer.setDebt(240.0);

        Order order = setupOrder(customer);

        Order oldOrder = Order.builder()
                .id(1L)
                .customer(customer)
                .date(LocalDate.now())
                .deliveredBy(Order.DeliveredBy.FIRST)
                .payed(false)
                .items(new ArrayList<>())
                .build();

        OrderItem oldOrderItem = OrderItem.builder()
                .articleName("Voda 19L")
                .articlePrice(200.0)
                .articleTax(20.0)
                .quantity(1)
                .build();

        when(orderRepository.findById(any(Long.class))).thenReturn(oldOrder);
        when(orderRepository.update(order)).thenReturn(Optional.of(order));
        when(customerService.update(any(Customer.class))).thenAnswer(invocation -> Optional.of(invocation.getArguments()[0]));
        doNothing().when(orderItemService).deleteAllByOrderId(any(Long.class));
        when(orderItemService.getAllByOrderId(any(Long.class))).thenReturn(Collections.singletonList(oldOrderItem));
        when(orderItemService.insert(any(OrderItem.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

        orderService.update(order);

        Mockito.verify(customerService).update(customer);
        assertEquals(Integer.valueOf(2), customer.getPackagingLarge());
        assertEquals(Double.valueOf(480.0), customer.getDebt());
    }


    @Test
    void deleteAllTest() {
        doNothing().when(orderRepository).deleteById(any(Order.class));
        when(orderRepository.findById(any(Long.class))).thenReturn(setupOrder(setupCustomer()));
        orderService.deleteAll(Collections.singletonList(setupOrder(setupCustomer())));

        Mockito.verify(orderRepository).deleteById(Mockito.any());
        Mockito.verify(customerService).update(Mockito.any());
    }

    @Test
    void deleteAllDtoTest() {
        doNothing().when(orderRepository).deleteById(any(Order.class));
        when(orderRepository.findById(any(Long.class))).thenReturn(setupOrder(setupCustomer()));

        orderService.deleteAllDto(Collections.singletonList(OrderDto.builder().id(1L).deliveredBy(Order.DeliveredBy.FIRST.toString()).build()));
        //Delivered orders are not deleted
        Mockito.verify(customerService, times(0)).update(Mockito.any());
        Mockito.verify(orderRepository, times(0)).deleteById(Mockito.any());

        orderService.deleteAllDto(Collections.singletonList(OrderDto.builder().id(1L).deliveredBy(Order.DeliveredBy.NONE.toString()).build()));

        Mockito.verify(orderRepository).deleteById(Mockito.any());
        Mockito.verify(customerService).update(Mockito.any());
        Mockito.verify(customerService, times(1)).update(Mockito.any());
        Mockito.verify(orderRepository, times(1)).deleteById(Mockito.any());

    }

    private Order setupOrder(Customer customer) {
        Order order = Order.builder()
                .id(1L)
                .customer(customer)
                .date(LocalDate.now())
                .deliveredBy(Order.DeliveredBy.FIRST)
                .payed(false)
                .items(new ArrayList<>())
                .build();

        OrderItem orderItem = OrderItem.builder()
                .articleName("Voda 19L")
                .articlePrice(200.0)
                .articleTax(20.0)
                .quantity(2)
                .build();

        order.addItem(orderItem);
        return order;
    }

    private Customer setupCustomer() {
        return Customer.builder()
                .name("Misa Misic")
                .address("Adresa")
                .city("Beograd")
                .phone("123123")
                .date(LocalDate.of(2021, 2, 6))
                .legalForm(Customer.LegalForm.LEGAL_ENTITY)
                .requiredSanitisePeriodInMonths(3)
                .debt(0.0)
                .packagingSmall(0)
                .packagingLarge(0)
                .build();
    }
}
