package com.nautilus.repository;

import com.nautilus.domain.Customer;

import java.util.List;

public interface CustomerRepository extends BaseRepository<Customer, Long> {
    List<Customer> findByTextFields(String text);

    List<String> findDistinctCities(String text);
}
