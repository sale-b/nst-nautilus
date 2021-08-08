package com.nautilus.repository;

import com.nautilus.domain.Customer;
import com.nautilus.domain.Sanitize;
import config.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@Sql(statements = {"delete from customer"}, executionPhase = AFTER_TEST_METHOD)
public class SanitizeRepositoryTest extends BaseTest {

    @Autowired
    SanitizeRepository sanitizeRepository;

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
        assertEquals(0, sanitizeRepository.getAll().size());

        Sanitize sanitize = Sanitize.builder()
                .date(LocalDate.of(2021, 5, 5))
                .customer(customer)
                .build();

        sanitizeRepository.insert(sanitize);

        assertNotNull(sanitize);
        assertNotNull(sanitize.getId());
        assertNotNull(sanitize.getCreatedOn());
        assertNotNull(sanitize.getModifiedOn());
        assertEquals(customer, sanitize.getCustomer());
        assertEquals(LocalDate.of(2021, 5, 5), sanitize.getDate());
        assertEquals(LocalDate.of(2021, 5, 5), sanitize.getDate());
        assertEquals(sanitize.getCreatedOn(), sanitize.getModifiedOn());
        assertEquals(1, sanitizeRepository.getAll().size());
    }

    @Test
    void updateTest() {
        assertEquals(0, sanitizeRepository.getAll().size());

        Sanitize sanitize = Sanitize.builder()
                .date(LocalDate.of(2021, 5, 5))
                .customer(customer)
                .build();

        sanitizeRepository.insert(sanitize);

        LocalDateTime modifiedOld = sanitize.getModifiedOn();
        assertNotNull(sanitize);
        assertNotNull(sanitize.getId());
        assertNotNull(sanitize.getCreatedOn());
        assertNotNull(sanitize.getModifiedOn());
        sanitize.setDate(LocalDate.of(2020, 7, 7));

        sanitizeRepository.update(sanitize);

        assertNotNull(sanitize);
        assertNotNull(sanitize.getId());
        assertNotNull(sanitize.getCreatedOn());
        assertNotNull(sanitize.getModifiedOn());
        assertEquals(customer, sanitize.getCustomer());
        assertEquals(LocalDate.of(2020, 7, 7), sanitize.getDate());
        assertNotEquals(modifiedOld, sanitize.getModifiedOn());
        assertEquals(1, sanitizeRepository.getAll().size());

        sanitize.setModifiedOn(LocalDateTime.now());
        assertFalse(sanitizeRepository.update(sanitize).isPresent());
    }

    @Test
    void deleteByIdTest() {
        assertEquals(0, sanitizeRepository.getAll().size());

        Sanitize sanitize = Sanitize.builder()
                .date(LocalDate.of(2021, 5, 5))
                .customer(customer)
                .build();

        sanitizeRepository.insert(sanitize);
        assertEquals(1, sanitizeRepository.getAll().size());
        sanitizeRepository.deleteById(sanitize);
        assertEquals(0, sanitizeRepository.getAll().size());
    }

    @Test
    void deleteAllTest() {
        assertEquals(0, sanitizeRepository.getAll().size());

        Sanitize sanitizeOne = Sanitize.builder()
                .date(LocalDate.of(2021, 5, 5))
                .customer(customer)
                .build();

        Sanitize sanitizeTwo = Sanitize.builder()
                .date(LocalDate.of(2021, 7, 7))
                .customer(customer)
                .build();

        sanitizeRepository.insert(sanitizeOne);
        sanitizeRepository.insert(sanitizeTwo);


        assertEquals(2, sanitizeRepository.getAll().size());
        sanitizeRepository.deleteAll(sanitizeRepository.getAll());
        assertEquals(0, sanitizeRepository.getAll().size());
    }

    @Test
    void findByIdTest() {
        assertEquals(0, sanitizeRepository.getAll().size());

        Sanitize sanitize = Sanitize.builder()
                .date(LocalDate.of(2021, 5, 5))
                .customer(customer)
                .build();

        sanitizeRepository.insert(sanitize);
        sanitize = sanitizeRepository.findById(sanitize.getId());

        assertNotNull(sanitize);
        assertNotNull(sanitize.getId());
        assertNotNull(sanitize.getCreatedOn());
        assertNotNull(sanitize.getModifiedOn());
        assertEquals(customer, sanitize.getCustomer());
        assertEquals(LocalDate.of(2021, 5, 5), sanitize.getDate());
        assertEquals(LocalDate.of(2021, 5, 5), sanitize.getDate());
        assertEquals(1, sanitizeRepository.getAll().size());
    }

}


