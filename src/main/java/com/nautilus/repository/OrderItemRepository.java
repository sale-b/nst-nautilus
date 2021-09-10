package com.nautilus.repository;

import com.nautilus.domain.OrderItem;

import java.util.List;

public interface OrderItemRepository extends BaseRepository<OrderItem, Long> {

    List<OrderItem> getAllByOrderId(Long orderId);

    void deleteAllByOrderId(Long orderId);

}
