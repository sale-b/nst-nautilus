package com.nautilus.service;

import com.nautilus.domain.OrderItem;
import com.nautilus.repository.OrderItemRepository;
import com.nautilus.service.impl.OrderItemServiceImpl;
import config.BaseTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
public class OrderItemServiceTest extends BaseTest {

    @Mock
    OrderItemRepository orderItemRepository;

    @InjectMocks
    OrderItemServiceImpl orderItemService;

    @Test
    void getAllTest() {
        orderItemService.getAll();

        Mockito.verify(orderItemRepository).getAll();
    }

    @Test
    void insertTest() {
        orderItemService.insert(new OrderItem());

        Mockito.verify(orderItemRepository).insert(Mockito.any());
    }

    @Test
    void findByIdTest() {
        orderItemService.findById(1L);

        Mockito.verify(orderItemRepository).findById(1L);
    }

    @Test
    void updateTest() {
        orderItemService.update(new OrderItem());

        Mockito.verify(orderItemRepository).update(Mockito.any());
    }

    @Test
    void deleteAllTest() {
        orderItemService.deleteAll(new ArrayList<>());

        Mockito.verify(orderItemRepository).deleteAll(Mockito.any());
        Mockito.verify(orderItemRepository, times(1)).deleteAll(Mockito.any());
    }

    @Test
    void getAllByOrderIdTest() {
        orderItemService.getAllByOrderId(1L);

        Mockito.verify(orderItemRepository).getAllByOrderId(Mockito.any());
    }

    @Test
    void deleteAllByOrderIdTest() {
        orderItemService.deleteAllByOrderId(1L);

        Mockito.verify(orderItemRepository).deleteAllByOrderId(Mockito.any());
    }


}
