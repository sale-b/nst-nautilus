package com.nautilus.repository.impl;

import com.nautilus.domain.Article;
import com.nautilus.repository.ArticleRepository;
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

    private final static String SELECT_ALL_ARTICLES = "SELECT * from article order by id";
    private final static String SELECT_ARTICLE_BY_ID = "select * from article where id = ?";
    private final static String INSERT_ARTICLE = "INSERT into article (name, price) VALUES (?,?)";
    private final static String UPDATE_ARTICLE_BY_ID = "UPDATE article SET name=?, price=?, modified_on=current_timestamp WHERE id=? and modified_on=?";
    private final static String DELETE_ARTICLE_BY_ID = "DELETE FROM article WHERE id=? AND mandatory=false";
    private final static String SELECT_ARTICLES_BY_TEXT_FIELDS = "select * from article where lower(name) like ?";


    private final JdbcTemplate jdbcTemplate;

    public ArticleRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Article insert(Article article) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_ARTICLE, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, article.getName());
            ps.setDouble(2, article.getPrice());
            return ps;
        }, keyHolder);

        article.setId((Long) (Objects.requireNonNull(keyHolder.getKeys()).get("id")));
        return article;
    }

    @Transactional
    @Override
    public List<Article> getAll() {
        return jdbcTemplate.query(SELECT_ALL_ARTICLES, new ArticleMapper());
    }

    @Transactional
    @Override
    public Article findById(Long id) {
        return jdbcTemplate.queryForObject(SELECT_ARTICLE_BY_ID, new ArticleMapper(), id);
    }

    @Transactional
    @Override
    public Optional<Article> update(Article article) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(UPDATE_ARTICLE_BY_ID, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, article.getName());
            ps.setDouble(2, article.getPrice());
            ps.setLong(3, article.getId());
            ps.setTimestamp(4, Timestamp.valueOf(article.getModifiedOn()));
            return ps;
        }, keyHolder);
        if (keyHolder.getKeys() == null)
            return Optional.empty();
        return Optional.of(article);
    }

    @Transactional
    @Override
    public void deleteById(Article article) {
        jdbcTemplate.update(DELETE_ARTICLE_BY_ID,
                article.getId());
    }

    @Override
    public List<Article> findByTextFields(String text) {
        return jdbcTemplate.query(SELECT_ARTICLES_BY_TEXT_FIELDS, new ArticleMapper(), "%" + text + "%");
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
                    rs.getBoolean("mandatory"),
                    rs.getTimestamp("created_on") != null ? rs.getTimestamp("created_on").toLocalDateTime() : null,
                    rs.getTimestamp("modified_on") != null ? rs.getTimestamp("modified_on").toLocalDateTime() : null
            );
        }

    }
}
