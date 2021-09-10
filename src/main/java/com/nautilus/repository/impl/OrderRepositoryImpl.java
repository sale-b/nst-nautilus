package com.nautilus.repository.impl;

import com.nautilus.domain.Customer;
import com.nautilus.domain.Order;
import com.nautilus.domain.dto.OrderDto;
import com.nautilus.repository.OrderRepository;
import com.nautilus.util.Queries;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

import static com.nautilus.util.Formatter.formatPrice;


/**
 * @author Aleksandar Brankovic
 */
@Repository
@Transactional
@Slf4j
public class OrderRepositoryImpl implements OrderRepository {

    private final String QUERIES_FILE = "dbqueries/order-queries.properties";
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public OrderRepositoryImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Order insert(Order order) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(Queries.getQuery(QUERIES_FILE, "INSERT_ORDER"), Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, order.getCustomer().getId());
            ps.setDate(2, Date.valueOf(order.getDate()));
            ps.setString(3, order.getDeliveredBy().getStringValue());
            ps.setBoolean(4, order.getPayed());
            ps.setString(5, order.getNote());
            return ps;
        }, keyHolder);

        order.setId((Long) (Objects.requireNonNull(keyHolder.getKeys()).get("id")));
        return order;
    }

    @Transactional
    @Override
    public List<Order> getAll() {
        return jdbcTemplate.query(Queries.getQuery(QUERIES_FILE, "SELECT_ALL_ORDERS"), new OrderMapper());
    }

    @Transactional
    @Override
    public List<OrderDto> getAllDto(LocalDate date, String city) {
        StringBuilder query = new StringBuilder(Queries.getQuery(QUERIES_FILE, "SELECT_ALL_ORDERS_DTO") + " ");
        Map<String, Object> parameters = new HashMap<>();
        if (city != null && !city.equals("")) {
            query.append(Queries.getQuery(QUERIES_FILE, "FILTER_BY_CITY_PART")).append(" ");
            parameters.put("city", city);
        }
        if (date != null) {
            query.append(Queries.getQuery(QUERIES_FILE, "FILTER_BY_DATE_PART")).append(" ");
            parameters.put("date", date);
        }
        query.append(Queries.getQuery(QUERIES_FILE, "FILTER_ORDERS_END_PART"));

        return namedParameterJdbcTemplate.query(query.toString(), parameters, new OrderDtoMapper());
    }

    @Transactional
    @Override
    public Order findById(Long id) {
        return jdbcTemplate.queryForObject(Queries.getQuery(QUERIES_FILE, "SELECT_ORDER_BY_ID"), new OrderMapper(), id);
    }

    @Transactional
    @Override
    public Optional<Order> update(Order order) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(Queries.getQuery(QUERIES_FILE, "UPDATE_ORDER_BY_ID"), Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, order.getCustomer().getId());
            ps.setDate(2, Date.valueOf(order.getDate()));
            ps.setString(3, order.getDeliveredBy().getStringValue());
            ps.setBoolean(4, order.getPayed());
            ps.setString(5, order.getNote());
            ps.setLong(6, order.getId());
            ps.setTimestamp(7, Timestamp.valueOf(order.getModifiedOn()));
            return ps;
        }, keyHolder);
        if (keyHolder.getKeys() == null)
            return Optional.empty();
        return Optional.of(order);
    }

    @Transactional
    @Override
    public void deleteById(Order order) {
        jdbcTemplate.update(Queries.getQuery(QUERIES_FILE, "DELETE_ORDER_BY_ID"),
                order.getId());
    }

    @Transactional
    @Override
    public void deleteAll(Iterable<Order> orders) {
        orders.forEach(this::deleteById);
    }

    @Transactional
    @Override
    public Optional<Order> getCustomersLastOrder(Customer customer) {
        Order lastOrder = null;
        try {
            lastOrder = jdbcTemplate.queryForObject(Queries.getQuery(QUERIES_FILE, "SELECT_LAST_ORDER_BY_CUSTOMER_ID"), new OrderMapper(), customer.getId());
        } catch (EmptyResultDataAccessException e) {
            log.error("Last order is not found!");
        }
        return Optional.ofNullable(lastOrder);
    }

    static class OrderMapper implements RowMapper<Order> {

        @Override
        public Order mapRow(@NonNull ResultSet rs, int i) throws SQLException {
            Customer customer = CustomerRepositoryImpl.CustomerMapper.mapCustomer(rs, i);
            return new Order(
                    rs.getLong("order_id"),
                    customer,
                    rs.getDate("order_date").toLocalDate(),
                    new ArrayList<>(),
                    Order.DeliveredBy.valueOf(rs.getString("delivered_by")),
                    rs.getBoolean("payed"),
                    rs.getString("note"),
                    rs.getTimestamp("order_created_on") != null ? rs.getTimestamp("order_created_on").toLocalDateTime() : null,
                    rs.getTimestamp("order_modified_on") != null ? rs.getTimestamp("order_modified_on").toLocalDateTime() : null
            );
        }
    }

    static class OrderDtoMapper implements RowMapper<OrderDto> {

        @Override
        public OrderDto mapRow(@NonNull ResultSet rs, int i) throws SQLException {
            Customer customer = CustomerRepositoryImpl.CustomerMapper.mapCustomer(rs, i);
            return OrderDto.builder()
                    .id(rs.getLong("order_id"))
                    .name(customer.getName())
                    .city(customer.getCity())
                    .address(customer.getAddress())
                    .phone(customer.getPhone())
                    .legalForm(customer.getLegalForm())
                    .waterSmall(Integer.toString(rs.getInt("water_small")))
                    .waterLarge(Integer.toString(rs.getInt("water_large")))
                    .glasses(Integer.toString(rs.getInt("glasses")))
                    .deliveredBy(Order.DeliveredBy.valueOf(rs.getString("delivered_by")).equals(Order.DeliveredBy.NONE) ? "Nije isporučeno" : Order.DeliveredBy.valueOf(rs.getString("delivered_by")).toString())
                    .payed(rs.getBoolean("payed") ? "DA" : "NE")
                    .note(rs.getString("note"))
                    .totalPrice(formatPrice(rs.getDouble("total_price")))
                    .build();
        }

    }

}
