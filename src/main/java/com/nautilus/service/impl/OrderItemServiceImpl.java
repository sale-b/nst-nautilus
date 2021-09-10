package com.nautilus.service.impl;

import com.nautilus.domain.OrderItem;
import com.nautilus.repository.OrderItemRepository;
import com.nautilus.service.OrderItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    final
    OrderItemRepository orderItemRepository;

    public OrderItemServiceImpl(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public OrderItem insert(OrderItem obj) {
        return orderItemRepository.insert(obj);
    }

    @Override
    public List<OrderItem> getAll() {
        return orderItemRepository.getAll();
    }

    @Override
    public OrderItem findById(Long id) {
        return orderItemRepository.findById(id);
    }

    @Override
    public Optional<OrderItem> update(OrderItem obj) {
        return orderItemRepository.update(obj);
    }

    @Override
    public void deleteAll(Iterable<OrderItem> obj) {
        orderItemRepository.deleteAll(obj);
    }

    @Override
    public List<OrderItem> getAllByOrderId(Long orderId) {
        return orderItemRepository.getAllByOrderId(orderId);
    }

    @Override
    public void deleteAllByOrderId(Long orderId) {
        orderItemRepository.deleteAllByOrderId(orderId);
    }
}
