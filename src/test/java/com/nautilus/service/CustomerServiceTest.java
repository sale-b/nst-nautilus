package com.nautilus.service;

import com.nautilus.domain.Customer;
import com.nautilus.domain.dto.CustomerDto;
import com.nautilus.repository.CustomerRepository;
import com.nautilus.service.impl.CustomerServiceImpl;
import config.BaseTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest extends BaseTest {

    @Mock
    CustomerRepository customerRepository;

    @InjectMocks
    CustomerServiceImpl customerService;

    @Test
    void getAllTest() {
        customerService.getAll();

        Mockito.verify(customerRepository).getAll();
    }

    @Test
    void findByIdTest() {
        customerService.findById(1L);

        Mockito.verify(customerRepository).findById(1L);
    }

    @Test
    void updateTest() {
        customerService.update(new Customer());

        Mockito.verify(customerRepository).update(Mockito.any());
    }

    @Test
    void insertTest() {
        customerService.insert(new Customer());

        Mockito.verify(customerRepository).insert(Mockito.any());
    }

    @Test
    void findByTextFieldsTest() {
        customerService.findByTextFields("test");

        Mockito.verify(customerRepository).findByTextFields(Mockito.any());
    }

    @Test
    void findDtoByTextFieldsTest() {
        customerService.findDtoByTextFields("test");

        Mockito.verify(customerRepository).findDtoByTextFields(Mockito.any());
    }

    @Test
    void findDistinctCitiesTest() {
        customerService.findDistinctCities("test");
        Mockito.verify(customerRepository).findDistinctCities(Mockito.any());

        customerService.findDistinctCities();
        Mockito.verify(customerRepository).findDistinctCities();
    }

    @Test
    void deleteAllDtoTest() {
        customerService.deleteAllDto(Collections.singletonList(CustomerDto.builder().id(1L).build()));

        Mockito.verify(customerRepository).deleteAll(Mockito.any());
        Mockito.verify(customerRepository, times(1)).deleteAll(Mockito.any());
    }

    @Test
    void getAllDtoTest() {
        customerService.getAllDto();

        Mockito.verify(customerRepository).getAllDto();
    }

    @Test
    void getDtoWithUnfulfilledObligationTest() {
        customerService.getDtoWithUnfulfilledObligation();

        Mockito.verify(customerRepository).getDtoWithUnfulfilledObligation();
    }

    @Test
    void getDtoWithSanitizeNeededTest() {
        customerService.getDtoWithSanitizeNeeded();

        Mockito.verify(customerRepository).getDtoWithSanitizeNeeded();
    }

    @Test
    void countDtoWithSanitizeNeededTest() {
        customerService.countDtoWithSanitizeNeeded();

        Mockito.verify(customerRepository).countDtoWithSanitizeNeeded();
    }

    @Test
    void countDtoWithUnfulfilledObligationTest() {
        customerService.countDtoWithUnfulfilledObligation();

        Mockito.verify(customerRepository).countDtoWithUnfulfilledObligation();
    }

    @Test
    void getDtoWithSanitizeLateTest() {
        customerService.getDtoWithSanitizeLate();

        Mockito.verify(customerRepository).getDtoWithSanitizeLate();
    }

    @Test
    void countDtoWithSanitizeLateTest() {
        customerService.countDtoWithSanitizeLate();

        Mockito.verify(customerRepository).countDtoWithSanitizeLate();
    }

    @Test
    void getDtoWithPackagingDebtTest() {
        customerService.getDtoWithPackagingDebt();

        Mockito.verify(customerRepository).getDtoWithPackagingDebt();
    }

    @Test
    void countDtoWithPackagingDebtTest() {
        customerService.countDtoWithPackagingDebt();

        Mockito.verify(customerRepository).countDtoWithPackagingDebt();
    }

    @Test
    void getDtoWithDebtTest() {
        customerService.getDtoWithDebt();

        Mockito.verify(customerRepository).getDtoWithDebt();
    }

    @Test
    void countDtoWithDebtTest() {
        customerService.countDtoWithDebt();

        Mockito.verify(customerRepository).countDtoWithDebt();
    }

    @Test
    void selectDatesWithUnfulfilledObligationForCustomerTest() {
        customerService.selectDatesWithUnfulfilledObligationForCustomer(new CustomerDto());

        Mockito.verify(customerRepository).selectDatesWithUnfulfilledObligationForCustomer(Mockito.any());
    }


}
