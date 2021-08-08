package com.nautilus.repository;

import com.nautilus.domain.Customer;
import com.nautilus.domain.dto.CustomerDto;

import java.time.LocalDate;
import java.util.List;

public interface CustomerRepository extends BaseRepository<Customer, Long> {
    List<CustomerDto> findDtoByTextFields(String text);

    List<Customer> findByTextFields(String text);

    List<String> findDistinctCities(String text);

    List<String> findDistinctCities();

    List<CustomerDto> getAllDto();

    List<CustomerDto> getDtoWithUnfulfilledObligation();

    Integer countDtoWithUnfulfilledObligation();

    List<CustomerDto> getDtoWithSanitizeNeeded();

    Integer countDtoWithSanitizeNeeded();

    List<CustomerDto> getDtoWithSanitizeLate();

    Integer countDtoWithSanitizeLate();

    List<CustomerDto> getDtoWithPackagingDebt();

    Integer countDtoWithPackagingDebt();

    List<CustomerDto> getDtoWithDebt();

    Integer countDtoWithDebt();

    List<LocalDate> selectDatesWithUnfulfilledObligationForCustomer(CustomerDto obj);
}
