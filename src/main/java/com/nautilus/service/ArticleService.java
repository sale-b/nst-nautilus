package com.nautilus.service;

import com.nautilus.domain.Article;

import java.util.List;

public interface ArticleService extends BaseService<Article, Long> {
    List<Article> findByTextFields(String text);
}