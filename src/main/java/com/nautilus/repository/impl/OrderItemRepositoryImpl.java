package com.nautilus.repository.impl;

import com.nautilus.domain.OrderItem;
import com.nautilus.repository.OrderItemRepository;
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
public class OrderItemRepositoryImpl implements OrderItemRepository {

    private final String QUERIES_FILE = "dbqueries/order-item-queries.properties";

    private final JdbcTemplate jdbcTemplate;

    public OrderItemRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public OrderItem insert(OrderItem orderItem) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(Queries.getQuery(QUERIES_FILE,"INSERT_ORDER_ITEM"), Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, orderItem.getArticleName());
            ps.setDouble(2, orderItem.getArticlePrice());
            ps.setDouble(3, orderItem.getArticleTax());
            ps.setInt(4, orderItem.getQuantity());
            ps.setLong(5, orderItem.getOrder().getId());
            return ps;
        }, keyHolder);

        orderItem.setId((Long) (Objects.requireNonNull(keyHolder.getKeys()).get("id")));
        return orderItem;
    }

    @Transactional
    @Override
    public Optional<OrderItem> update(OrderItem orderItem) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(Queries.getQuery(QUERIES_FILE,"UPDATE_ORDER_ITEM_BY_ID"), Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, orderItem.getArticleName());
            ps.setDouble(2, orderItem.getArticlePrice());
            ps.setDouble(3, orderItem.getArticleTax());
            ps.setInt(4, orderItem.getQuantity());
            ps.setLong(5, orderItem.getOrder().getId());
            ps.setLong(6, orderItem.getId());
            ps.setTimestamp(7, Timestamp.valueOf(orderItem.getModifiedOn()));
            return ps;
        }, keyHolder);
        if (keyHolder.getKeys() == null)
            return Optional.empty();
        return Optional.of(orderItem);
    }

    @Transactional
    @Override
    public List<OrderItem> getAll() {
        return jdbcTemplate.query(Queries.getQuery(QUERIES_FILE,"SELECT_ALL_ORDER_ITEMS"), new OrderItemMapper());
    }

    @Transactional
    @Override
    public List<OrderItem> getAllByOrderId(Long orderId) {
        return jdbcTemplate.query(Queries.getQuery(QUERIES_FILE,"SELECT_ALL_ORDER_ITEMS_BY_ORDER_ID"), new OrderItemMapper(), orderId);
    }

    @Transactional
    @Override
    public void deleteAllByOrderId(Long orderId) {
        jdbcTemplate.update(Queries.getQuery(QUERIES_FILE,"DELETE_ORDER_ITEM_BY_ORDER_ID"),
                orderId);
    }

    @Transactional
    @Override
    public OrderItem findById(Long id) {
        return jdbcTemplate.queryForObject(Queries.getQuery(QUERIES_FILE,"SELECT_ORDER_ITEM_BY_ID"), new OrderItemMapper(), id);
    }

    @Transactional
    @Override
    public void deleteById(OrderItem orderItem) {
        jdbcTemplate.update(Queries.getQuery(QUERIES_FILE,"DELETE_ORDER_ITEM_BY_ID"),
                orderItem.getId());
    }

    @Transactional
    @Override
    public void deleteAll(Iterable<OrderItem> orderItems) {
        orderItems.forEach(this::deleteById);
    }

    static class OrderItemMapper implements RowMapper<OrderItem> {

        @Override
        public OrderItem mapRow(ResultSet rs, int i) throws SQLException {
            return new OrderItem(
                    rs.getLong("id"),
                    rs.getString("article_name"),
                    rs.getDouble("article_price"),
                    rs.getDouble("article_tax"),
                    rs.getInt("quantity"),
                    null,
                    rs.getTimestamp("created_on") != null ? rs.getTimestamp("created_on").toLocalDateTime() : null,
                    rs.getTimestamp("modified_on") != null ? rs.getTimestamp("modified_on").toLocalDateTime() : null
            );
        }

    }
}
