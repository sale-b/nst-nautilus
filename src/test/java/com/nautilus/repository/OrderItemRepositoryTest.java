package com.nautilus.repository;

import com.nautilus.domain.Customer;
import com.nautilus.domain.Order;
import com.nautilus.domain.OrderItem;
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
public class OrderItemRepositoryTest extends BaseTest {

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    CustomerRepository customerRepository;

    private Order order;

    @BeforeEach
    void setUp() {
        Customer customer = customerRepository.insert(Customer.builder()
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

        this.order = Order.builder()
                .customer(customer)
                .date(LocalDate.now())
                .deliveredBy(Order.DeliveredBy.FIRST)
                .payed(false)
                .items(new ArrayList<>())
                .build();

        orderRepository.insert(order);
    }

    @Test
    void insertTest() {
        assertEquals(0, orderItemRepository.getAll().size());

        OrderItem orderItem = OrderItem.builder()
                .articleName("Article Test 1")
                .articlePrice(200.0)
                .articleTax(20.0)
                .quantity(3)
                .build();

        order.addItem(orderItem);
        orderItemRepository.insert(orderItem);

        assertNotNull(orderItem);
        assertNotNull(orderItem.getId());
        assertNotNull(orderItem.getCreatedOn());
        assertNotNull(orderItem.getModifiedOn());
        assertEquals("Article Test 1", orderItem.getArticleName());
        assertEquals(Double.valueOf(200.0), orderItem.getArticlePrice());
        assertEquals(Double.valueOf(20), orderItem.getArticleTax());
        assertEquals(Integer.valueOf(3), orderItem.getQuantity());
        assertEquals(1, orderItemRepository.getAll().size());
    }

    @Test
    void updateTest() {
        assertEquals(0, orderItemRepository.getAll().size());
        OrderItem orderItem = OrderItem.builder()
                .articleName("OrderItem Test 1")
                .articlePrice(200.5)
                .articleTax(20.0)
                .quantity(3)
                .build();

        order.addItem(orderItem);
        orderItemRepository.insert(orderItem);

        LocalDateTime modifiedOld = orderItem.getModifiedOn();
        assertNotNull(orderItem);
        assertNotNull(orderItem.getId());
        assertNotNull(orderItem.getCreatedOn());
        assertNotNull(orderItem.getModifiedOn());
        orderItem.setArticleName("OrderItem Test 2");
        orderItem.setArticlePrice(300.0);
        orderItem.setArticleTax(10.0);

        orderItemRepository.update(orderItem);
        assertEquals(1, orderItemRepository.getAll().size());
        assertEquals("OrderItem Test 2", orderItem.getArticleName());
        assertEquals(Double.valueOf(300.0), orderItem.getArticlePrice());
        assertEquals(Double.valueOf(10.0), orderItem.getArticleTax());
        assertEquals(1, orderItemRepository.getAll().size());
        assertEquals(orderItem.getId(), orderItem.getId());
        assertEquals(orderItem.getCreatedOn(), orderItem.getCreatedOn());
        assertNotEquals(modifiedOld, orderItem.getModifiedOn());

        orderItem.setModifiedOn(LocalDateTime.now());
        assertFalse(orderItemRepository.update(orderItem).isPresent());
    }

    @Test
    void deleteByIdTest() {
        assertEquals(0, orderItemRepository.getAll().size());
        OrderItem orderItem = OrderItem.builder()
                .articleName("OrderItem Test 1")
                .articlePrice(200.5)
                .articleTax(20.0)
                .quantity(3)
                .build();

        order.addItem(orderItem);
        orderItemRepository.insert(orderItem);
        orderItemRepository.deleteById(orderItem);
        assertEquals(0, orderItemRepository.getAll().size());
    }

    @Test
    void deleteAllTest() {
        assertEquals(0, orderItemRepository.getAll().size());
        OrderItem orderItemOne = OrderItem.builder()
                .articleName("OrderItem Test 1")
                .articlePrice(200.5)
                .articleTax(20.0)
                .quantity(3)
                .build();

        OrderItem orderItemTwo = OrderItem.builder()
                .articleName("OrderItem Test 2")
                .articlePrice(300.0)
                .articleTax(20.0)
                .quantity(3)
                .build();

        order.addItem(orderItemOne);
        order.addItem(orderItemTwo);
        orderItemRepository.insert(orderItemOne);
        orderItemRepository.insert(orderItemTwo);

        assertEquals(2, orderItemRepository.getAll().size());
        orderItemRepository.deleteAll(orderItemRepository.getAll());
        assertEquals(0, orderItemRepository.getAll().size());
    }

    @Test
    void findByIdTest() {
        assertEquals(0, orderItemRepository.getAll().size());
        OrderItem orderItem = OrderItem.builder()
                .articleName("OrderItem Test 1")
                .articlePrice(200.5)
                .articleTax(20.0)
                .quantity(3)
                .build();

        order.addItem(orderItem);
        orderItemRepository.insert(orderItem);
        orderItem = orderItemRepository.findById(orderItem.getId());
        assertNotNull(orderItem);
        assertNotNull(orderItem.getId());
        assertNotNull(orderItem.getCreatedOn());
        assertNotNull(orderItem.getModifiedOn());
        assertEquals(1, orderItemRepository.getAll().size());
        assertEquals("OrderItem Test 1", orderItem.getArticleName());
        assertEquals(Double.valueOf(200.5), orderItem.getArticlePrice());
        assertEquals(Double.valueOf(20.0), orderItem.getArticleTax());
        assertEquals(1, orderItemRepository.getAll().size());
    }

    @Test
    void getAllByOrderIdTest() {
        assertEquals(0, orderItemRepository.getAll().size());
        OrderItem orderItemOne = OrderItem.builder()
                .articleName("OrderItem Test 1")
                .articlePrice(200.5)
                .articleTax(20.0)
                .quantity(3)
                .build();

        OrderItem orderItemTwo = OrderItem.builder()
                .articleName("OrderItem Test 2")
                .articlePrice(300.0)
                .articleTax(20.0)
                .quantity(3)
                .build();

        order.addItem(orderItemOne);
        order.addItem(orderItemTwo);
        orderItemRepository.insert(orderItemOne);
        orderItemRepository.insert(orderItemTwo);

        assertEquals(2, orderItemRepository.getAllByOrderId(order.getId()).size());
    }

    @Test
    void deleteAllByOrderIdTest() {
        assertEquals(0, orderItemRepository.getAll().size());
        OrderItem orderItemOne = OrderItem.builder()
                .articleName("OrderItem Test 1")
                .articlePrice(200.5)
                .articleTax(20.0)
                .quantity(3)
                .build();

        OrderItem orderItemTwo = OrderItem.builder()
                .articleName("OrderItem Test 2")
                .articlePrice(300.0)
                .articleTax(20.0)
                .quantity(3)
                .build();

        order.addItem(orderItemOne);
        order.addItem(orderItemTwo);
        orderItemRepository.insert(orderItemOne);
        orderItemRepository.insert(orderItemTwo);

        assertEquals(2, orderItemRepository.getAll().size());
        orderItemRepository.deleteAllByOrderId(order.getId());
        assertEquals(0, orderItemRepository.getAll().size());
    }

}


