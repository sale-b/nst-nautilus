package com.nautilus.repository.impl;

import com.nautilus.domain.Customer;
import com.nautilus.domain.Order;
import com.nautilus.domain.dto.OrderDto;
import com.nautilus.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * @author Aleksandar Brankovic
 */
@Repository
@Transactional
@Slf4j
public class OrderRepositoryImpl implements OrderRepository {
    private final static String SELECT_ALL_ORDERS_DTO = "WITH bottles15 AS (SELECT order_id, quantity FROM order_item WHERE article_name = 'Voda 15L'),\n" +
            "bottles19 AS (SELECT order_id, quantity FROM order_item WHERE article_name = 'Voda 19L'),\n" +
            "glasses AS (SELECT order_id, quantity FROM order_item WHERE article_name = 'Čaše'),\n" +
            "total_price AS (select order_id, SUM(article_price * quantity) as total_price from order_item group by order_id)\n" +
            "SELECT customer.*, \"order\".id order_id, \"order\".\"date\" order_date, \"order\".delivered_by, \"order\".payed, \"order\".packaging_debt_small, \"order\".packaging_debt_large, \"order\".note, \"order\".created_on order_created_on, \"order\".modified_on order_modified_on,   COALESCE(bottles15.quantity, 0) water_small, COALESCE(bottles19.quantity, 0) water_large, COALESCE(glasses.quantity, 0) glasses, COALESCE(total_price.total_price, 0) total_price\n" +
            "FROM \"order\"\n" +
            "JOIN customer ON \"order\".customer_id = customer.id\n" +
            "LEFT JOIN bottles15 on bottles15.order_id = \"order\".id\n" +
            "LEFT JOIN bottles19 on bottles19.order_id = \"order\".id\n" +
            "LEFT JOIN glasses on glasses.order_id = \"order\".id\n" +
            "LEFT JOIN total_price on total_price.order_id = \"order\".id\n" +
            "ORDER BY \"order\".\"date\" DESC";
    private final static String SELECT_ALL_ORDERS = "SELECT customer.*, \"order\".id order_id, \"order\".\"date\" order_date, \"order\".delivered_by, \"order\".payed, \"order\".packaging_debt_small, \"order\".packaging_debt_large, \"order\".note, \"order\".created_on order_created_on, \"order\".modified_on order_modified_on\n" +
            "FROM \"order\"\n" +
            "JOIN customer ON \"order\".customer_id = customer.id order by \"order\".\"date\" DESC";
    private final static String SELECT_ORDER_BY_ID = "SELECT customer.*, \"order\".id order_id, \"order\".\"date\" order_date, \"order\".delivered_by, \"order\".payed, \"order\".packaging_debt_small, \"order\".packaging_debt_large, \"order\".note, \"order\".created_on order_created_on, \"order\".modified_on order_modified_on\n" +
            "FROM \"order\"\n" +
            "JOIN customer ON \"order\".customer_id = customer.id where \"order\".id=?";
    private final static String INSERT_ORDER = "INSERT into \"order\" (customer_id, date, delivered_by, payed, packaging_debt_small, packaging_debt_large, note) VALUES (?,?,?,?,?,?,?)";
    private final static String UPDATE_ORDER_BY_ID = "UPDATE \"order\" SET customer_id=?, date=?, delivered_by=?, payed=?, packaging_debt_small=?, packaging_debt_large=?, note=?, modified_on=current_timestamp WHERE id=? and modified_on=?";
    private final static String DELETE_ORDER_BY_ID = "DELETE FROM \"order\" WHERE id=?";
    private final static String SELECT_ALL_BY_CUSTOMER_ID_ORDER_BY_DATE = "SELECT \"order\".id order_id, \"order\".\"date\" order_date, \"order\".delivered_by, \"order\".payed, \"order\".packaging_debt_small, \"order\".packaging_debt_large, \"order\".note, \"order\".created_on order_created_on, \"order\".modified_on order_modified_on\n" +
            " FROM \"order\" WHERE customer_id=? ORDER BY date";
    private final static String SELECT_LAST_ORDER_BY_CUSTOMER_ID = "SELECT \"order\".id order_id, \"order\".\"date\" order_date, \"order\".delivered_by, \"order\".payed, \"order\".packaging_debt_small, \"order\".packaging_debt_large, \"order\".note, \"order\".created_on order_created_on, \"order\".modified_on order_modified_on\n" +
            "FROM \"order\" WHERE customer_id=? ORDER BY date DESC LIMIT 1";
    private final JdbcTemplate jdbcTemplate;

    public OrderRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Order insert(Order order) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_ORDER, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, order.getCustomer().getId());
            ps.setDate(2, Date.valueOf(order.getDate()));
            ps.setString(3, order.getDeliveredBy().getStringValue());
            ps.setBoolean(4, order.getPayed());
            ps.setInt(5, order.getPackagingDebtSmall());
            ps.setInt(6, order.getPackagingDebtLarge());
            ps.setString(7, order.getNote());
            return ps;
        }, keyHolder);

        order.setId((Long) (Objects.requireNonNull(keyHolder.getKeys()).get("id")));
        return order;
    }

    @Transactional
    @Override
    public List<Order> getAll() {
        return jdbcTemplate.query(SELECT_ALL_ORDERS, new OrderMapper());
    }

    @Transactional
    @Override
    public List<OrderDto> getAllDto() {
        return jdbcTemplate.query(SELECT_ALL_ORDERS_DTO, new OrderDtoMapper());
    }

    @Transactional
    @Override
    public Order findById(Long id) {
        return jdbcTemplate.queryForObject(SELECT_ORDER_BY_ID, new OrderMapper(), id);
    }

    @Transactional
    @Override
    public Optional<Order> update(Order order) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(UPDATE_ORDER_BY_ID, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, order.getCustomer().getId());
            ps.setDate(2, Date.valueOf(order.getDate()));
            ps.setString(3, order.getDeliveredBy().getStringValue());
            ps.setBoolean(4, order.getPayed());
            ps.setInt(5, order.getPackagingDebtSmall());
            ps.setInt(6, order.getPackagingDebtLarge());
            ps.setString(7, order.getNote());
            ps.setLong(8, order.getId());
            ps.setTimestamp(9, Timestamp.valueOf(order.getModifiedOn()));
            return ps;
        }, keyHolder);
        if (keyHolder.getKeys() == null)
            return Optional.empty();
        return Optional.of(order);
    }

    @Transactional
    @Override
    public void deleteById(Order order) {
        jdbcTemplate.update(DELETE_ORDER_BY_ID,
                order.getId());
    }

    @Transactional
    @Override
    public void deleteAll(Iterable<Order> orders) {
        orders.forEach(this::deleteById);
    }

    @Transactional
    @Override
    public List<Order> getAllByCustomerIdOrderByDate(Long customerId) {
        return jdbcTemplate.query(SELECT_ALL_BY_CUSTOMER_ID_ORDER_BY_DATE, new OrderMapper(), customerId);
    }

    @Transactional
    @Override
    public Optional<Order> getCustomersLastOrder(Long customerId) {
        return Optional.ofNullable(jdbcTemplate.queryForObject(SELECT_LAST_ORDER_BY_CUSTOMER_ID, new OrderMapper(), customerId));
    }

    static class OrderMapper implements RowMapper<Order> {

        @Override
        public Order mapRow(ResultSet rs, int i) throws SQLException {
            Customer customer = mapCustomer(rs, i);
            return new Order(
                    rs.getLong("order_id"),
                    customer,
                    rs.getDate("order_date").toLocalDate(),
                    new ArrayList<>(),
                    Order.DeliveredBy.valueOf(rs.getString("delivered_by")),
                    rs.getBoolean("payed"),
                    rs.getInt("packaging_debt_small"),
                    rs.getInt("packaging_debt_large"),
                    rs.getString("note"),
                    rs.getTimestamp("order_created_on") != null ? rs.getTimestamp("order_created_on").toLocalDateTime() : null,
                    rs.getTimestamp("order_modified_on") != null ? rs.getTimestamp("order_modified_on").toLocalDateTime() : null
            );
        }
    }

        static class OrderDtoMapper implements RowMapper<OrderDto> {

            @Override
            public OrderDto mapRow(ResultSet rs, int i) throws SQLException {
                Customer customer = mapCustomer(rs, i);
                return OrderDto.builder()
                        .id(rs.getLong("order_id"))
                        .name(customer.getName())
                        .city(customer.getCity())
                        .address(customer.getAddress())
                        .phone(customer.getPhone())
                        .waterSmall(Integer.toString(rs.getInt("water_small")))
                        .waterLarge(Integer.toString(rs.getInt("water_large")))
                        .glasses(Integer.toString(rs.getInt("glasses")))
                        .deliveredBy(Order.DeliveredBy.valueOf(rs.getString("delivered_by")).equals(Order.DeliveredBy.NONE) ? "Nije isporučeno" : Order.DeliveredBy.valueOf(rs.getString("delivered_by")).toString())
                        .payed(rs.getBoolean("payed") ? "DA" : "NE")
                        .note(rs.getString("note"))
                        .totalPrice(String.format("%.02f", rs.getDouble("total_price")))
                        .build();
            }

        }


    private static Customer mapCustomer(ResultSet rs, int i) throws SQLException {
        CustomerRepositoryImpl.CustomerMapper cm = new CustomerRepositoryImpl.CustomerMapper();
        Customer customer;
        try {
            customer = cm.mapRow(rs, i);
        } catch (PSQLException e) {
            customer = new Customer();
        }
        return customer;
    }
}
