package com.nautilus.service;


import com.nautilus.domain.Order;
import com.nautilus.domain.dto.OrderDto;

import java.util.List;

public interface OrderService extends BaseService<Order, Long> {

    void deleteAllDto(Iterable<OrderDto> obj);

    List<Order> getAllByCustomerIdOrderByDate(Long customerId);

    List<OrderDto> getAllDto();

}
