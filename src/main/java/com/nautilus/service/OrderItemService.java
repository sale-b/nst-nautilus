package com.nautilus.service;


import com.nautilus.domain.OrderItem;

import java.util.List;

public interface OrderItemService extends BaseService<OrderItem, Long> {
    List<OrderItem> getAllByOrderId(Long orderId);

    void deleteAllByOrderId(Long orderId);
}
