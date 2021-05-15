package com.nautilus.repository.impl;

import com.nautilus.domain.Customer;
import com.nautilus.repository.CustomerRepository;
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
public class CustomerRepositoryImpl implements CustomerRepository {

    private final static String SELECT_ALL_CUSTOMERS = "SELECT * from customer order by name";
    private final static String SELECT_CUSTOMER_BY_ID = "select * from customer where id = ?";
    private final static String INSERT_CUSTOMER = "INSERT into customer (name, city, address, phone, sanitise_period, date, type) VALUES (?,?,?,?,?,?,?)";
    private final static String UPDATE_CUSTOMER_BY_ID = "UPDATE customer SET name=?, address=?, city=?, phone=?, sanitise_period=?, date=?, type=?, debt=?, packaging_small=?, packaging_large=?, backlog_packaging_small=?, backlog_packaging_large=?, modified_on=current_timestamp WHERE id=? and modified_on=?";
    private final static String DELETE_CUSTOMER_BY_ID = "DELETE FROM customer WHERE id=?";
    private final static String SELECT_CUSTOMERS_BY_TEXT_FIELDS = "select * from customer where id::varchar(255) like ? or lower(name) like ? or lower(address) like ? or lower(phone) like ? or lower(type) like ? or lower(city) like ?";
    private final static String SELECT_DISTINCT_CITIES_BY_NAME = "select distinct city from customer where lower(city) like ? order by city";

    private final JdbcTemplate jdbcTemplate;

    public CustomerRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Customer insert(Customer customer) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_CUSTOMER, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getCity());
            ps.setString(3, customer.getAddress());
            ps.setString(4, customer.getPhone());
            ps.setInt(5, customer.getSanitisePeriodInMonths());
            ps.setDate(6, Date.valueOf(customer.getDate()));
            ps.setString(7, customer.getType().serialize());
            return ps;
        }, keyHolder);

        customer.setId((Long) Objects.requireNonNull(keyHolder.getKeys()).get("id"));
        return customer;
    }

    @Transactional
    @Override
    public List<Customer> getAll() {
        return jdbcTemplate.query(SELECT_ALL_CUSTOMERS, new CustomerMapper());
    }

    @Transactional
    @Override
    public Customer findById(Long id) {
        return jdbcTemplate.queryForObject(SELECT_CUSTOMER_BY_ID, new CustomerMapper(), id);
    }

    @Transactional
    @Override
    public Optional<Customer> update(Customer customer) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(UPDATE_CUSTOMER_BY_ID, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getAddress());
            ps.setString(3, customer.getCity());
            ps.setString(4, customer.getPhone());
            ps.setInt(5, customer.getSanitisePeriodInMonths());
            ps.setDate(6, Date.valueOf(customer.getDate()));
            ps.setString(7, customer.getType().serialize());
            ps.setDouble(8, customer.getDebt());
            ps.setInt(9, customer.getPackagingSmall());
            ps.setInt(10, customer.getPackagingLarge());
            ps.setBoolean(11, customer.getBacklogPackagingSmall());
            ps.setBoolean(12, customer.getBacklogPackagingLarge());
            ps.setLong(13, customer.getId());
            ps.setTimestamp(14, Timestamp.valueOf(customer.getModifiedOn()));
            return ps;
        }, keyHolder);
        if (keyHolder.getKeys() == null)
            return Optional.empty();
        return Optional.of(customer);
    }

    @Transactional
    @Override
    public void deleteById(Customer customer) {
        jdbcTemplate.update(DELETE_CUSTOMER_BY_ID,
                customer.getId());
    }

    @Override
    public List<Customer> findByTextFields(String text) {
        return jdbcTemplate.query(SELECT_CUSTOMERS_BY_TEXT_FIELDS, new CustomerMapper(), "%" + text + "%", "%" + text + "%", "%" + text + "%", "%" + text + "%", "%" + text + "%", "%" + text + "%");
    }

    @Override
    public List<String> findDistinctCities(String text) {
        return jdbcTemplate.query(SELECT_DISTINCT_CITIES_BY_NAME, (rs, rowNum) -> rs.getString(1), "%" + text + "%");
    }

    @Transactional
    @Override
    public void deleteAll(Iterable<Customer> customers) {
        customers.forEach(this::deleteById);
    }

    static class CustomerMapper implements RowMapper<Customer> {

        @Override
        public Customer mapRow(ResultSet rs, int i) throws SQLException {
            return new Customer(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("city"),
                    rs.getString("address"),
                    rs.getString("phone"),
                    rs.getDate("date").toLocalDate(),
                    Customer.CustomerType.valueOf(rs.getString("type")),
                    rs.getInt("sanitise_period"),
                    rs.getDouble("debt"),
                    rs.getInt("packaging_small"),
                    rs.getInt("packaging_large"),
                    rs.getBoolean("backlog_packaging_small"),
                    rs.getBoolean("backlog_packaging_large"),
                    rs.getTimestamp("created_on") != null ? rs.getTimestamp("created_on").toLocalDateTime() : null,
                    rs.getTimestamp("modified_on") != null ? rs.getTimestamp("modified_on").toLocalDateTime() : null
            );
        }

    }
}
