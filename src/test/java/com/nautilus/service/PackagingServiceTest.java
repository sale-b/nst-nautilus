package com.nautilus.service;

import com.nautilus.domain.Customer;
import com.nautilus.domain.Packaging;
import com.nautilus.repository.PackagingRepository;
import com.nautilus.service.impl.PackagingServiceImpl;
import config.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class PackagingServiceTest extends BaseTest {

    @Mock
    PackagingRepository packagingRepository;

    @Mock
    CustomerService customerService;

    @InjectMocks
    PackagingServiceImpl packagingService;

    @Test
    void getAllTest() {
        packagingService.getAll();

        Mockito.verify(packagingRepository).getAll();
    }

    @Test
    void insertTest() {
        Customer customer = setupCustomer();
        assertEquals(Integer.valueOf(1), customer.getPackagingSmall());
        assertEquals(Integer.valueOf(1), customer.getPackagingLarge());

        Packaging packaging = setupPackaging(customer);

        when(packagingRepository.insert(packaging)).thenReturn(packaging);
        when(customerService.update(any(Customer.class))).thenAnswer(invocation -> Optional.of(invocation.getArguments()[0]));

        packagingService.insert(packaging);

        Mockito.verify(packagingRepository).insert(packaging);
        Mockito.verify(customerService).update(customer);
        assertEquals(Integer.valueOf(0), customer.getPackagingSmall());
        assertEquals(Integer.valueOf(0), customer.getPackagingLarge());
    }

    @Test
    void insertExceptionTest() {
        Customer customer = setupCustomer();
        customer.setPackagingLarge(0);
        assertEquals(Integer.valueOf(1), customer.getPackagingSmall());
        assertEquals(Integer.valueOf(0), customer.getPackagingLarge());

        Packaging packaging = setupPackaging(customer);

        Assertions.assertThrows(RuntimeException.class, () -> packagingService.insert(packaging));

    }

    @Test
    void findByIdTest() {
        packagingService.findById(1L);

        Mockito.verify(packagingRepository).findById(1L);
    }

    @Test
    void updateTest() {
        Customer customer = setupCustomer();
        customer.setPackagingSmall(0);
        customer.setPackagingLarge(1);

        assertEquals(Integer.valueOf(0), customer.getPackagingSmall());
        assertEquals(Integer.valueOf(1), customer.getPackagingLarge());

        Packaging packaging = setupPackaging(customer);

        Packaging oldPackaging = Packaging.builder()
                .id(1L)
                .customer(customer)
                .date(LocalDate.now())
                .waterLargeReturned(0)
                .waterSmallReturned(1)
                .build();

        when(packagingRepository.findById(any(Long.class))).thenReturn(oldPackaging);
        when(packagingRepository.update(packaging)).thenReturn(Optional.of(packaging));
        when(customerService.update(any(Customer.class))).thenAnswer(invocation -> Optional.of(invocation.getArguments()[0]));

        packagingService.update(packaging);

        Mockito.verify(customerService).update(customer);
        assertEquals(Integer.valueOf(0), customer.getPackagingSmall());
        assertEquals(Integer.valueOf(0), customer.getPackagingLarge());
    }

    @Test
    void deleteAllTest() {
        doNothing().when(packagingRepository).deleteById(any(Packaging.class));
        packagingService.deleteAll(Collections.singletonList(setupPackaging(setupCustomer())));

        Mockito.verify(packagingRepository).deleteById(Mockito.any());
        Mockito.verify(customerService).update(Mockito.any());
    }

    private Packaging setupPackaging(Customer customer) {
        return Packaging.builder()
                .id(1L)
                .customer(customer)
                .date(LocalDate.now())
                .waterLargeReturned(1)
                .waterSmallReturned(1)
                .build();
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
                .packagingSmall(1)
                .packagingLarge(1)
                .build();
    }

}
