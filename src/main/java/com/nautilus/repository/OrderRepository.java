package com.nautilus.repository;

import com.nautilus.domain.Order;
import com.nautilus.domain.dto.OrderDto;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends BaseRepository<Order, Long> {

    List<OrderDto> getAllDto(LocalDate date, String city);

}
