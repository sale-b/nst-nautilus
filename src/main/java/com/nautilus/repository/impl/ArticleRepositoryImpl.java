package com.nautilus.repository.impl;

import com.nautilus.domain.Article;
import com.nautilus.repository.ArticleRepository;
import com.nautilus.util.Queries;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * @author Aleksandar Brankovic
 */
@Repository
@Transactional
@Slf4j
public class ArticleRepositoryImpl implements ArticleRepository {


    private final String QUERIES_FILE = "dbqueries/article-queries.properties";

    private final JdbcTemplate jdbcTemplate;

    public ArticleRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Article insert(Article article) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(Queries.getQuery(QUERIES_FILE, "INSERT_ARTICLE"), Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, article.getName());
            ps.setDouble(2, article.getPrice());
            ps.setDouble(3, article.getTax());
            return ps;
        }, keyHolder);

        article.setId((Long) (Objects.requireNonNull(keyHolder.getKeys()).get("id")));
        article.setCreatedOn(((Timestamp) Objects.requireNonNull(keyHolder.getKeys()).get("created_on")).toLocalDateTime());
        article.setModifiedOn(((Timestamp) Objects.requireNonNull(keyHolder.getKeys()).get("modified_on")).toLocalDateTime());
        return article;
    }

    @Transactional
    @Override
    public List<Article> getAll() {
        return jdbcTemplate.query(Queries.getQuery(QUERIES_FILE, "SELECT_ALL_ARTICLES"), new ArticleMapper());
    }

    @Transactional
    @Override
    public Article findById(Long id) {
        return jdbcTemplate.queryForObject(Queries.getQuery(QUERIES_FILE, "SELECT_ARTICLE_BY_ID"), new ArticleMapper(), id);
    }

    @Transactional
    @Override
    public Optional<Article> update(Article article) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(Queries.getQuery(QUERIES_FILE, "UPDATE_ARTICLE_BY_ID"), Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, article.getName());
            ps.setDouble(2, article.getPrice());
            ps.setDouble(3, article.getTax());
            ps.setLong(4, article.getId());
            ps.setTimestamp(5, Timestamp.valueOf(article.getModifiedOn()));
            return ps;
        }, keyHolder);
        if (keyHolder.getKeys() == null) {
            return Optional.empty();
        } else {
            article.setModifiedOn(((Timestamp) Objects.requireNonNull(keyHolder.getKeys()).get("modified_on")).toLocalDateTime());
        }
        return Optional.of(article);
    }

    @Transactional
    @Override
    public void deleteById(Article article) {
        jdbcTemplate.update(Queries.getQuery(QUERIES_FILE, "DELETE_ARTICLE_BY_ID"),
                article.getId());
    }

    @Override
    public List<Article> findByTextFields(String text) {
        return jdbcTemplate.query(Queries.getQuery(QUERIES_FILE, "SELECT_ARTICLES_BY_TEXT_FIELDS"), new ArticleMapper(), "%" + text + "%");
    }

    @Transactional
    @Override
    public void deleteAll(Iterable<Article> articles) {
        articles.forEach(this::deleteById);
    }

    static class ArticleMapper implements RowMapper<Article> {

        @Override
        public Article mapRow(ResultSet rs, int i) throws SQLException {
            return new Article(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getDouble("tax"),
                    rs.getBoolean("mandatory"),
                    rs.getTimestamp("created_on") != null ? rs.getTimestamp("created_on").toLocalDateTime() : null,
                    rs.getTimestamp("modified_on") != null ? rs.getTimestamp("modified_on").toLocalDateTime() : null
            );
        }

    }
}
