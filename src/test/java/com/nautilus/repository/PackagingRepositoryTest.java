package com.nautilus.repository;

import com.nautilus.domain.Customer;
import com.nautilus.domain.Packaging;
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
public class PackagingRepositoryTest extends BaseTest {

    @Autowired
    PackagingRepository packagingRepository;

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
        assertEquals(0, packagingRepository.getAll().size());

        Packaging packaging = Packaging.builder()
                .date(LocalDate.of(2021, 5, 5))
                .waterSmallReturned(3)
                .waterLargeReturned(2)
                .customer(customer)
                .build();

        packagingRepository.insert(packaging);

        assertNotNull(packaging);
        assertNotNull(packaging.getId());
        assertNotNull(packaging.getCreatedOn());
        assertNotNull(packaging.getModifiedOn());
        assertEquals(customer, packaging.getCustomer());
        assertEquals(LocalDate.of(2021, 5, 5), packaging.getDate());
        assertEquals(Integer.valueOf(3), packaging.getWaterSmallReturned());
        assertEquals(Integer.valueOf(2), packaging.getWaterLargeReturned());
        assertEquals(LocalDate.of(2021, 5, 5), packaging.getDate());
        assertEquals(packaging.getCreatedOn(), packaging.getModifiedOn());
        assertEquals(1, packagingRepository.getAll().size());
    }

    @Test
    void updateTest() {
        assertEquals(0, packagingRepository.getAll().size());

        Packaging packaging = Packaging.builder()
                .date(LocalDate.of(2021, 5, 5))
                .waterSmallReturned(3)
                .waterLargeReturned(2)
                .customer(customer)
                .build();

        packagingRepository.insert(packaging);

        LocalDateTime modifiedOld = packaging.getModifiedOn();
        assertNotNull(packaging);
        assertNotNull(packaging.getId());
        assertNotNull(packaging.getCreatedOn());
        assertNotNull(packaging.getModifiedOn());
        packaging.setDate(LocalDate.of(2020, 7, 7));
        packaging.setWaterSmallReturned(5);
        packaging.setWaterLargeReturned(5);

        packagingRepository.update(packaging);

        assertNotNull(packaging);
        assertNotNull(packaging.getId());
        assertNotNull(packaging.getCreatedOn());
        assertNotNull(packaging.getModifiedOn());
        assertEquals(customer, packaging.getCustomer());
        assertEquals(LocalDate.of(2020, 7, 7), packaging.getDate());
        assertEquals(Integer.valueOf(5), packaging.getWaterSmallReturned());
        assertEquals(Integer.valueOf(5), packaging.getWaterLargeReturned());
        assertNotEquals(modifiedOld, packaging.getModifiedOn());
        assertEquals(1, packagingRepository.getAll().size());

        packaging.setModifiedOn(LocalDateTime.now());
        assertFalse(packagingRepository.update(packaging).isPresent());
    }

    @Test
    void deleteByIdTest() {
        assertEquals(0, packagingRepository.getAll().size());

        Packaging packaging = Packaging.builder()
                .date(LocalDate.of(2021, 5, 5))
                .waterSmallReturned(3)
                .waterLargeReturned(2)
                .customer(customer)
                .build();

        packagingRepository.insert(packaging);
        assertEquals(1, packagingRepository.getAll().size());
        packagingRepository.deleteById(packaging);
        assertEquals(0, packagingRepository.getAll().size());
    }

    @Test
    void deleteAllTest() {
        assertEquals(0, packagingRepository.getAll().size());

        Packaging packagingOne = Packaging.builder()
                .date(LocalDate.of(2021, 5, 5))
                .waterSmallReturned(3)
                .waterLargeReturned(2)
                .customer(customer)
                .build();

        Packaging packagingTwo = Packaging.builder()
                .date(LocalDate.of(2021, 7, 7))
                .waterSmallReturned(7)
                .waterLargeReturned(7)
                .customer(customer)
                .build();

        packagingRepository.insert(packagingOne);
        packagingRepository.insert(packagingTwo);


        assertEquals(2, packagingRepository.getAll().size());
        packagingRepository.deleteAll(packagingRepository.getAll());
        assertEquals(0, packagingRepository.getAll().size());
    }

    @Test
    void findByIdTest() {
        assertEquals(0, packagingRepository.getAll().size());

        Packaging packaging = Packaging.builder()
                .date(LocalDate.of(2021, 5, 5))
                .waterSmallReturned(3)
                .waterLargeReturned(2)
                .customer(customer)
                .build();

        packagingRepository.insert(packaging);
        packaging = packagingRepository.findById(packaging.getId());

        assertNotNull(packaging);
        assertNotNull(packaging.getId());
        assertNotNull(packaging.getCreatedOn());
        assertNotNull(packaging.getModifiedOn());
        assertEquals(customer, packaging.getCustomer());
        assertEquals(LocalDate.of(2021, 5, 5), packaging.getDate());
        assertEquals(Integer.valueOf(3), packaging.getWaterSmallReturned());
        assertEquals(Integer.valueOf(2), packaging.getWaterLargeReturned());
        assertEquals(LocalDate.of(2021, 5, 5), packaging.getDate());
        assertEquals(1, packagingRepository.getAll().size());
    }

}


