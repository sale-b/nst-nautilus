package com.nautilus.repository.impl;

import com.nautilus.domain.OrderItem;
import com.nautilus.repository.OrderItemRepository;
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

    private final static String SELECT_ALL_ORDER_ITEMS = "SELECT * from order_item order by id";
    private final static String SELECT_ALL_ORDER_ITEMS_BY_ORDER_ID = "SELECT * from order_item where order_id = ? order by id";
    private final static String SELECT_ORDER_ITEM_BY_ID = "select * from order_item where id = ?";
    private final static String INSERT_ORDER_ITEM = "INSERT into order_item (article_name, article_price, quantity, order_id) VALUES (?,?,?,?)";
    private final static String UPDATE_ORDER_ITEM_BY_ID = "UPDATE order_item SET article_name=?, article_price=?, quantity=?,order_id=?, modified_on=current_timestamp WHERE id=? and modified_on=?";
    private final static String DELETE_ORDER_ITEM_BY_ID = "DELETE FROM order_item WHERE id=?";
    private final static String DELETE_ORDER_ITEM_BY_ORDER_ID = "DELETE FROM order_item WHERE order_id=?";

    private final JdbcTemplate jdbcTemplate;

    public OrderItemRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public OrderItem insert(OrderItem orderItem) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_ORDER_ITEM, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, orderItem.getArticleName());
            ps.setDouble(2, orderItem.getArticlePrice());
            ps.setInt(3, orderItem.getQuantity());
            ps.setLong(4, orderItem.getOrder().getId());
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
                    .prepareStatement(UPDATE_ORDER_ITEM_BY_ID, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, orderItem.getArticleName());
            ps.setDouble(2, orderItem.getArticlePrice());
            ps.setInt(3, orderItem.getQuantity());
            ps.setLong(4, orderItem.getOrder().getId());
            ps.setLong(5, orderItem.getId());
            ps.setTimestamp(6, Timestamp.valueOf(orderItem.getModifiedOn()));
            return ps;
        }, keyHolder);
        if (keyHolder.getKeys() == null)
            return Optional.empty();
        return Optional.of(orderItem);
    }

    @Transactional
    @Override
    public List<OrderItem> getAll() {
        return jdbcTemplate.query(SELECT_ALL_ORDER_ITEMS, new OrderItemMapper());
    }

    @Transactional
    @Override
    public List<OrderItem> getAllByOrderId(Long orderId) {
        return jdbcTemplate.query(SELECT_ALL_ORDER_ITEMS_BY_ORDER_ID, new OrderItemMapper(), orderId);
    }

    @Transactional
    @Override
    public void deleteAllByOrderId(Long orderId) {
        jdbcTemplate.update(DELETE_ORDER_ITEM_BY_ORDER_ID,
                orderId);
    }

    @Transactional
    @Override
    public OrderItem findById(Long id) {
        return jdbcTemplate.queryForObject(SELECT_ORDER_ITEM_BY_ID, new OrderItemMapper(), id);
    }

    @Transactional
    @Override
    public void deleteById(OrderItem orderItem) {
        jdbcTemplate.update(DELETE_ORDER_ITEM_BY_ID,
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
                    rs.getInt("quantity"),
                    null,
                    rs.getTimestamp("created_on") != null ? rs.getTimestamp("created_on").toLocalDateTime() : null,
                    rs.getTimestamp("modified_on") != null ? rs.getTimestamp("modified_on").toLocalDateTime() : null
            );
        }

    }
}
