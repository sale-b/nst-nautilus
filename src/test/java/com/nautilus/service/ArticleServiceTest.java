package com.nautilus.service;

import com.nautilus.domain.Article;
import com.nautilus.repository.ArticleRepository;
import com.nautilus.service.impl.ArticleServiceImpl;
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
public class ArticleServiceTest extends BaseTest {

    @Mock
    ArticleRepository articleRepository;

    @InjectMocks
    ArticleServiceImpl articleService;

    @Test
    void getAllTest() {
        articleService.getAll();

        Mockito.verify(articleRepository).getAll();
    }

    @Test
    void insertTest() {
        articleService.insert(new Article());

        Mockito.verify(articleRepository).insert(Mockito.any());
    }

    @Test
    void findByIdTest() {
        articleService.findById(1L);

        Mockito.verify(articleRepository).findById(1L);
    }

    @Test
    void updateTest() {
        articleService.update(new Article());

        Mockito.verify(articleRepository).update(Mockito.any());
    }

    @Test
    void deleteAllTest() {
        articleService.deleteAll(new ArrayList<>());

        Mockito.verify(articleRepository).deleteAll(Mockito.any());
        Mockito.verify(articleRepository, times(1)).deleteAll(Mockito.any());
    }

    @Test
    void findByTextFieldsTest() {
        articleService.findByTextFields("test");

        Mockito.verify(articleRepository).findByTextFields(Mockito.any());
    }

}
