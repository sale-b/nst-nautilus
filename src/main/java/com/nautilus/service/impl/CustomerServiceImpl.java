package com.nautilus.service.impl;

import com.nautilus.domain.Customer;
import com.nautilus.repository.CustomerRepository;
import com.nautilus.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    final
    CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer insert(Customer obj) {
        return customerRepository.insert(obj);
    }

    @Override
    public List<Customer> getAll() {
        return customerRepository.getAll();
    }

    @Override
    public Customer findById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public Optional<Customer> update(Customer customer) {
        return customerRepository.update(customer);
    }

    @Override
    public List<Customer> findByTextFields(String text) {
        return customerRepository.findByTextFields(text);
    }

    @Override
    public List<String> findDistinctCities(String text) {
        return customerRepository.findDistinctCities(text);
    }

    @Override
    public void deleteAll(Iterable<Customer> users) {
        customerRepository.deleteAll(users);
    }

}
