package com.nautilus.repository;

import com.nautilus.domain.Customer;
import com.nautilus.domain.Order;
import com.nautilus.domain.dto.OrderDto;
import config.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@Sql(statements = {"delete from customer"}, executionPhase = AFTER_TEST_METHOD)
public class OrderRepositoryTest extends BaseTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    CustomerRepository customerRepository;

    private Customer customer;

    @BeforeEach
    void setUp() {
        this.customer = customerRepository.insert(Customer.builder()
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
                .build());
    }

    @Test
    void insertTest() {
        assertEquals(0, orderRepository.getAll().size());

        Order order = Order.builder()
                .customer(customer)
                .date(LocalDate.now())
                .deliveredBy(Order.DeliveredBy.FIRST)
                .payed(false)
                .items(new ArrayList<>())
                .build();

        orderRepository.insert(order);

        assertNotNull(order);
        assertNotNull(order.getId());
        assertNotNull(order.getCreatedOn());
        assertNotNull(order.getModifiedOn());
        assertEquals(customer, order.getCustomer());
        assertNull(order.getNote());
        assertEquals(Order.DeliveredBy.FIRST, order.getDeliveredBy());
        assertEquals(false, order.getPayed());
        assertEquals(LocalDate.now(), order.getDate());
        assertEquals(order.getCreatedOn(), order.getModifiedOn());
        assertEquals(1, orderRepository.getAll().size());
    }

    @Test
    void updateTest() {
        assertEquals(0, orderRepository.getAll().size());

        Order order = Order.builder()
                .customer(customer)
                .date(LocalDate.now())
                .deliveredBy(Order.DeliveredBy.FIRST)
                .payed(false)
                .items(new ArrayList<>())
                .build();

        orderRepository.insert(order);

        LocalDateTime modifiedOld = order.getModifiedOn();
        assertNotNull(order);
        assertNotNull(order.getId());
        assertNotNull(order.getCreatedOn());
        assertNotNull(order.getModifiedOn());
        order.setNote("test note");
        order.setPayed(true);
        order.setDeliveredBy(Order.DeliveredBy.SECOND);

        orderRepository.update(order);

        assertNotNull(order);
        assertNotNull(order.getId());
        assertNotNull(order.getCreatedOn());
        assertNotNull(order.getModifiedOn());
        assertEquals(customer, order.getCustomer());
        assertEquals("test note", order.getNote());
        assertEquals(Order.DeliveredBy.SECOND, order.getDeliveredBy());
        assertEquals(true, order.getPayed());
        assertEquals(LocalDate.now(), order.getDate());
        assertNotEquals(modifiedOld, order.getModifiedOn());
        assertEquals(1, orderRepository.getAll().size());

        order.setModifiedOn(LocalDateTime.now());
        assertFalse(orderRepository.update(order).isPresent());
    }

    @Test
    void deleteByIdTest() {
        assertEquals(0, orderRepository.getAll().size());

        Order order = Order.builder()
                .customer(customer)
                .date(LocalDate.now())
                .deliveredBy(Order.DeliveredBy.FIRST)
                .payed(false)
                .items(new ArrayList<>())
                .build();

        orderRepository.insert(order);
        assertEquals(1, orderRepository.getAll().size());
        orderRepository.deleteById(order);
        assertEquals(1, orderRepository.getAll().size());

        order.setDeliveredBy(Order.DeliveredBy.NONE);
        orderRepository.update(order);
        orderRepository.deleteById(order);
        assertEquals(0, orderRepository.getAll().size());
    }

    @Test
    void deleteAllTest() {
        assertEquals(0, orderRepository.getAll().size());

        Order orderOne = Order.builder()
                .customer(customer)
                .date(LocalDate.now())
                .deliveredBy(Order.DeliveredBy.FIRST)
                .payed(false)
                .items(new ArrayList<>())
                .build();

        Order orderTwo = Order.builder()
                .customer(customer)
                .date(LocalDate.now())
                .deliveredBy(Order.DeliveredBy.NONE)
                .payed(false)
                .items(new ArrayList<>())
                .build();

        orderRepository.insert(orderOne);
        orderRepository.insert(orderTwo);


        assertEquals(2, orderRepository.getAll().size());
        orderRepository.deleteAll(orderRepository.getAll());
        //DELIVERED ORDERS CAN'T BE REMOVED
        assertEquals(1, orderRepository.getAll().size());
    }

    @Test
    void findByIdTest() {
        assertEquals(0, orderRepository.getAll().size());

        Order order = Order.builder()
                .customer(customer)
                .date(LocalDate.now())
                .deliveredBy(Order.DeliveredBy.FIRST)
                .payed(false)
                .items(new ArrayList<>())
                .build();

        orderRepository.insert(order);
        order = orderRepository.findById(order.getId());

        assertNotNull(order);
        assertNotNull(order.getId());
        assertNotNull(order.getCreatedOn());
        assertNotNull(order.getModifiedOn());
        assertEquals(customer, order.getCustomer());
        assertNull(order.getNote());
        assertEquals(Order.DeliveredBy.FIRST, order.getDeliveredBy());
        assertEquals(false, order.getPayed());
        assertEquals(LocalDate.now(), order.getDate());
        assertEquals(1, orderRepository.getAll().size());
    }

    @Test
    void getAllDtoTest() {
        assertEquals(0, orderRepository.getAll().size());
        Order order = Order.builder()
                .customer(customer)
                .date(LocalDate.now())
                .deliveredBy(Order.DeliveredBy.FIRST)
                .payed(false)
                .items(new ArrayList<>())
                .build();
        orderRepository.insert(order);
        OrderDto orderDto = orderRepository.getAllDto(LocalDate.now(), "Beograd".trim().toLowerCase()).get(0);

        assertNotNull(order);
        assertNotNull(order.getId());
        assertEquals("Misa Misic", customer.getName());
        assertEquals("Beograd", customer.getCity());
        assertEquals("Adresa", customer.getAddress());
        assertEquals("123123", customer.getPhone());
        assertEquals(LocalDate.of(2021, 2, 6), customer.getDate());
        assertEquals(Customer.LegalForm.LEGAL_ENTITY, customer.getLegalForm());
        assertEquals(Integer.valueOf(3), customer.getRequiredSanitisePeriodInMonths());
        assertEquals(Double.valueOf(0), customer.getDebt());
        assertEquals(Integer.valueOf(0), customer.getPackagingSmall());
        assertEquals(Integer.valueOf(0), customer.getPackagingLarge());
        assertNull(orderDto.getNote());
        assertEquals("1", orderDto.getDeliveredBy());
        assertEquals("NE", orderDto.getPayed());
        assertEquals(1, orderRepository.getAll().size());
    }


}


