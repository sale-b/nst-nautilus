package com.nautilus.repository;

import com.nautilus.domain.Customer;
import com.nautilus.domain.Order;
import com.nautilus.domain.dto.OrderDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends BaseRepository<Order, Long> {

    Optional<Order> getCustomersLastOrder(Customer customer);

    List<OrderDto> getAllDto(LocalDate date, String city);

}
