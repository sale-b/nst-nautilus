package com.nautilus.service;

import com.nautilus.domain.Customer;

import java.util.List;

public interface CustomerService extends BaseService<Customer, Long> {
    List<Customer> findByTextFields(String text);

    List<String> findDistinctCities(String text);
}