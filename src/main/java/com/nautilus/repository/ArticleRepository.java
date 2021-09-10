package com.nautilus.repository;

import com.nautilus.domain.Article;

import java.util.List;

public interface ArticleRepository extends BaseRepository<Article, Long> {
    List<Article> findByTextFields(String text);

}
