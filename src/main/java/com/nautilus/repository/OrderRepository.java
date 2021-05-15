package com.nautilus.repository;

import com.nautilus.domain.Order;
import com.nautilus.domain.dto.OrderDto;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends BaseRepository<Order, Long> {

    List<Order> getAllByCustomerIdOrderByDate(Long customerId);

    Optional<Order> getCustomersLastOrder(Long customerId);

    List<OrderDto> getAllDto ();

}
