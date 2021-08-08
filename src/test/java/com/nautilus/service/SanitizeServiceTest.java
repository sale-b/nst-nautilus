package com.nautilus.service;

import com.nautilus.domain.Sanitize;
import com.nautilus.repository.SanitizeRepository;
import com.nautilus.service.impl.SanitizeServiceImpl;
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
public class SanitizeServiceTest extends BaseTest {

    @Mock
    SanitizeRepository sanitizeRepository;

    @InjectMocks
    SanitizeServiceImpl sanitizeService;

    @Test
    void getAllTest() {
        sanitizeService.getAll();

        Mockito.verify(sanitizeRepository).getAll();
    }

    @Test
    void insertTest() {
        sanitizeService.insert(new Sanitize());

        Mockito.verify(sanitizeRepository).insert(Mockito.any());
    }

    @Test
    void findByIdTest() {
        sanitizeService.findById(1L);

        Mockito.verify(sanitizeRepository).findById(1L);
    }

    @Test
    void updateTest() {
        sanitizeService.update(new Sanitize());

        Mockito.verify(sanitizeRepository).update(Mockito.any());
    }

    @Test
    void deleteAllTest() {
        sanitizeService.deleteAll(new ArrayList<>());

        Mockito.verify(sanitizeRepository).deleteAll(Mockito.any());
        Mockito.verify(sanitizeRepository, times(1)).deleteAll(Mockito.any());
    }

}
