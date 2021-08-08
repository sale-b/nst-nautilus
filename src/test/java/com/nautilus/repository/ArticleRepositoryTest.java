package com.nautilus.repository;

import com.nautilus.domain.Article;
import config.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Sql(statements = "delete from article", executionPhase = AFTER_TEST_METHOD)
@Sql(statements = "delete from article", executionPhase = BEFORE_TEST_METHOD)
public class ArticleRepositoryTest extends BaseTest {

    @Autowired
    ArticleRepository articleRepository;

    @Test
    void insertTest() {
        assertEquals(0, articleRepository.getAll().size());
        Article article = Article.builder()
                .name("Article Test 1")
                .price(200.5)
                .tax(20.0)
                .build();

        articleRepository.insert(article);
        assertNotNull(article);
        assertNotNull(article.getId());
        assertNotNull(article.getCreatedOn());
        assertNotNull(article.getModifiedOn());
        assertEquals("Article Test 1", article.getName());
        assertEquals(Double.valueOf(200.5), article.getPrice());
        assertEquals(Double.valueOf(20), article.getTax());
        assertEquals(1, articleRepository.getAll().size());
    }

    @Test
    void updateTest() {
        assertEquals(0, articleRepository.getAll().size());
        Article article = Article.builder()
                .name("Article Test 1")
                .price(200.5)
                .tax(20.0)
                .build();

        article = articleRepository.insert(article);
        LocalDateTime modifiedOld = article.getModifiedOn();
        assertNotNull(article);
        assertNotNull(article.getId());
        assertNotNull(article.getCreatedOn());
        assertNotNull(article.getModifiedOn());
        article.setName("Article Test 2");
        article.setPrice(300.0);
        article.setTax(10.0);

        articleRepository.update(article);
        assertEquals(1, articleRepository.getAll().size());
        assertEquals("Article Test 2", article.getName());
        assertEquals(Double.valueOf(300.0), article.getPrice());
        assertEquals(Double.valueOf(10.0), article.getTax());
        assertEquals(1, articleRepository.getAll().size());
        assertEquals(article.getId(), article.getId());
        assertEquals(article.getCreatedOn(), article.getCreatedOn());
        assertNotEquals(modifiedOld, article.getModifiedOn());

        article.setModifiedOn(LocalDateTime.now());
        assertFalse(articleRepository.update(article).isPresent());
    }

    @Test
    void deleteByIdTest() {
        assertEquals(0, articleRepository.getAll().size());
        Article article = Article.builder()
                .name("Article Test 1")
                .price(200.5)
                .tax(20.0)
                .build();

        article = articleRepository.insert(article);
        assertEquals(1, articleRepository.getAll().size());
        articleRepository.deleteById(article);
        assertEquals(0, articleRepository.getAll().size());
    }

    @Test
    void deleteAllTest() {
        assertEquals(0, articleRepository.getAll().size());
        Article articleOne = Article.builder()
                .name("Article Test 1")
                .price(200.5)
                .tax(20.0)
                .build();

        Article articleTwo = Article.builder()
                .name("Article Test 2")
                .price(300.0)
                .tax(20.0)
                .build();

        articleRepository.insert(articleOne);
        articleRepository.insert(articleTwo);

        assertEquals(2, articleRepository.getAll().size());
        articleRepository.deleteAll(articleRepository.getAll());
        assertEquals(0, articleRepository.getAll().size());
    }

    @Test
    void findByIdTest() {
        assertEquals(0, articleRepository.getAll().size());
        Article article = Article.builder()
                .name("Article Test 1")
                .price(200.5)
                .tax(20.0)
                .build();

        articleRepository.insert(article);
        article = articleRepository.findById(article.getId());
        assertNotNull(article);
        assertNotNull(article.getId());
        assertNotNull(article.getCreatedOn());
        assertNotNull(article.getModifiedOn());
        assertEquals(1, articleRepository.getAll().size());
        assertEquals("Article Test 1", article.getName());
        assertEquals(Double.valueOf(200.5), article.getPrice());
        assertEquals(Double.valueOf(20.0), article.getTax());
        assertEquals(1, articleRepository.getAll().size());
    }

    @Test
    void findByTextFieldsTest() {
        assertEquals(0, articleRepository.getAll().size());
        Article article = Article.builder()
                .name("Article Test 1")
                .price(200.5)
                .tax(20.0)
                .build();
        articleRepository.insert(article);

        assertEquals(article.getId(), articleRepository.findByTextFields("Article".toLowerCase().trim()).get(0).getId());
        assertEquals(article.getId(), articleRepository.findByTextFields("Test".toLowerCase().trim()).get(0).getId());
        assertEquals(article.getId(), articleRepository.findByTextFields("icle").get(0).getId());
    }

}


