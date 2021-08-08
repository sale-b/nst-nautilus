package com.nautilus.service.impl;

import com.nautilus.domain.Customer;
import com.nautilus.domain.dto.CustomerDto;
import com.nautilus.repository.CustomerRepository;
import com.nautilus.service.CustomerService;
import com.nautilus.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    final
    CustomerRepository customerRepository;

    OrderService orderService;

    @Override
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

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
    public List<CustomerDto> getAllDto() {
        return customerRepository.getAllDto();
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
    public List<CustomerDto> findDtoByTextFields(String text) {
        return customerRepository.findDtoByTextFields(text);
    }

    @Override
    public List<CustomerDto> getDtoWithUnfulfilledObligation() {
        return customerRepository.getDtoWithUnfulfilledObligation();
    }

    @Override
    public Integer countDtoWithUnfulfilledObligation() {
        return customerRepository.countDtoWithUnfulfilledObligation();
    }

    @Override
    public List<CustomerDto> getDtoWithSanitizeNeeded() {
        return customerRepository.getDtoWithSanitizeNeeded();
    }

    @Override
    public Integer countDtoWithSanitizeNeeded() {
        return customerRepository.countDtoWithSanitizeNeeded();
    }

    @Override
    public List<CustomerDto> getDtoWithSanitizeLate() {
        return customerRepository.getDtoWithSanitizeLate();
    }

    @Override
    public Integer countDtoWithSanitizeLate() {
        return customerRepository.countDtoWithSanitizeLate();
    }

    @Override
    public List<CustomerDto> getDtoWithPackagingDebt() {
        return customerRepository.getDtoWithPackagingDebt();
    }

    @Override
    public Integer countDtoWithPackagingDebt() {
        return customerRepository.countDtoWithPackagingDebt();
    }

    @Override
    public List<CustomerDto> getDtoWithDebt() {
        return customerRepository.getDtoWithDebt();
    }

    @Override
    public Integer countDtoWithDebt() {
        return customerRepository.countDtoWithDebt();
    }

    @Override
    public List<LocalDate> selectDatesWithUnfulfilledObligationForCustomer(CustomerDto customerDto) {
        return customerRepository.selectDatesWithUnfulfilledObligationForCustomer(customerDto);
    }

    @Override
    public List<String> findDistinctCities(String text) {
        return customerRepository.findDistinctCities(text);
    }

    @Override
    public List<String> findDistinctCities() {
        return customerRepository.findDistinctCities();
    }

    @Override
    public void deleteAll(Iterable<Customer> users) {
        customerRepository.deleteAll(users);
    }

    @Override
    public void deleteAllDto(Iterable<CustomerDto> obj) {
        List<Customer> customers = new ArrayList<>();
        obj.forEach(customerDto -> customers.add(mapToCustomer(customerDto)));
        this.deleteAll(customers);
    }

    private Customer mapToCustomer(CustomerDto customerDto) {
        return Customer.builder()
                .id(customerDto.getId())
                .build();
    }

}
