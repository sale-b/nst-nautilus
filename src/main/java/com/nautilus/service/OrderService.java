package com.nautilus.service;


import com.nautilus.domain.Order;
import com.nautilus.domain.dto.OrderDto;

import java.time.LocalDate;
import java.util.List;

public interface OrderService extends BaseService<Order, Long> {

    void deleteAllDto(Iterable<OrderDto> obj);

    List<OrderDto> getAllDto(LocalDate date, String city);

}
