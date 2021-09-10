package com.nautilus.service.impl;

import com.nautilus.domain.Article;
import com.nautilus.repository.ArticleRepository;
import com.nautilus.service.ArticleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ArticleServiceImpl implements ArticleService {

    final
    ArticleRepository articleRepository;

    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public Article insert(Article obj) {
        return articleRepository.insert(obj);
    }

    @Override
    public List<Article> getAll() {
        return articleRepository.getAll();
    }

    @Override
    public Article findById(Long id) {
        return articleRepository.findById(id);
    }

    @Override
    public Optional<Article> update(Article Article) {
        return articleRepository.update(Article);
    }

    @Override
    public List<Article> findByTextFields(String text) {
        return articleRepository.findByTextFields(text);
    }

    @Override
    public void deleteAll(Iterable<Article> articles) {
        articleRepository.deleteAll(articles);
    }

}
