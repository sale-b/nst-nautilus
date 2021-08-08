package com.nautilus.repository.impl;

import com.nautilus.domain.Customer;
import com.nautilus.domain.dto.CustomerDto;
import com.nautilus.repository.CustomerRepository;
import com.nautilus.util.Queries;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.time.LocalDate;
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

    private final String QUERIES_FILE = "dbqueries/customer-queries.properties";

    private final JdbcTemplate jdbcTemplate;

    public CustomerRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Customer insert(Customer customer) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(Queries.getQuery(QUERIES_FILE, "INSERT_CUSTOMER"), Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getCity());
            ps.setString(3, customer.getAddress());
            ps.setString(4, customer.getPhone());
            ps.setObject(5, customer.getRequiredSanitisePeriodInMonths());
            ps.setDate(6, customer.getDate() != null ? Date.valueOf(customer.getDate()) : null);
            ps.setString(7, customer.getLegalForm().serialize());
            return ps;
        }, keyHolder);

        customer.setId((Long) Objects.requireNonNull(keyHolder.getKeys()).get("id"));
        customer.setCreatedOn(((Timestamp) Objects.requireNonNull(keyHolder.getKeys()).get("created_on")).toLocalDateTime());
        customer.setModifiedOn(((Timestamp) Objects.requireNonNull(keyHolder.getKeys()).get("modified_on")).toLocalDateTime());
        return customer;
    }

    @Transactional
    @Override
    public List<Customer> getAll() {
        return jdbcTemplate.query(Queries.getQuery(QUERIES_FILE, "SELECT_ALL_CUSTOMERS"), new CustomerMapper());
    }

    @Transactional
    @Override
    public List<CustomerDto> getAllDto() {
        return jdbcTemplate.query(Queries.getQuery(QUERIES_FILE, "SELECT_ALL_DTO_CUSTOMERS"), new CustomerDtoMapper());
    }

    @Transactional
    @Override
    public Customer findById(Long id) {
        return jdbcTemplate.queryForObject(Queries.getQuery(QUERIES_FILE, "SELECT_CUSTOMER_BY_ID"), new CustomerMapper(), id);
    }

    @Transactional
    @Override
    public Optional<Customer> update(Customer customer) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(Queries.getQuery(QUERIES_FILE, "UPDATE_CUSTOMER_BY_ID"), Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getAddress());
            ps.setString(3, customer.getCity());
            ps.setString(4, customer.getPhone());
            ps.setObject(5, customer.getRequiredSanitisePeriodInMonths());
            ps.setDate(6, customer.getDate() != null ? Date.valueOf(customer.getDate()) : null);
            ps.setString(7, customer.getLegalForm().serialize());
            ps.setDouble(8, customer.getDebt());
            ps.setInt(9, customer.getPackagingSmall());
            ps.setInt(10, customer.getPackagingLarge());
            ps.setLong(11, customer.getId());
            ps.setTimestamp(12, Timestamp.valueOf(customer.getModifiedOn()));
            return ps;
        }, keyHolder);
        if (keyHolder.getKeys() == null) {
            return Optional.empty();
        } else {
            customer.setModifiedOn(((Timestamp) Objects.requireNonNull(keyHolder.getKeys()).get("modified_on")).toLocalDateTime());
        }
        return Optional.of(customer);
    }

    @Transactional
    @Override
    public void deleteById(Customer customer) {
        jdbcTemplate.update(Queries.getQuery(QUERIES_FILE, "DELETE_CUSTOMER_BY_ID"),
                customer.getId());
    }

    @Override
    public List<Customer> findByTextFields(String text) {
        return jdbcTemplate.query(Queries.getQuery(QUERIES_FILE, "SELECT_CUSTOMERS_BY_TEXT_FIELDS"), new CustomerMapper(), "%" + text + "%", "%" + text + "%", "%" + text + "%", "%" + text + "%", "%" + text + "%");
    }

    @Override
    public List<CustomerDto> findDtoByTextFields(String text) {
        return jdbcTemplate.query(Queries.getQuery(QUERIES_FILE, "SELECT_CUSTOMERS_DTO_BY_TEXT_FIELDS"), new CustomerDtoMapper(), "%" + text + "%", "%" + text + "%", "%" + text + "%", "%" + text + "%", "%" + text + "%");
    }

    @Transactional
    @Override
    public List<CustomerDto> getDtoWithUnfulfilledObligation() {
        return jdbcTemplate.query(Queries.getQuery(QUERIES_FILE, "SELECT_CUSTOMERS_DTO_WITH_UNFULFILLED_OBLIGATION"), new CustomerDtoMapper());
    }

    @Override
    public Integer countDtoWithUnfulfilledObligation() {
        return jdbcTemplate.queryForObject(Queries.getQuery(QUERIES_FILE, "COUNT_CUSTOMERS_DTO_WITH_UNFULFILLED_OBLIGATION"), Integer.class);
    }

    @Override
    public List<CustomerDto> getDtoWithSanitizeNeeded() {
        return jdbcTemplate.query(Queries.getQuery(QUERIES_FILE, "SELECT_CUSTOMERS_DTO_WITH_SANITIZE_NEEDED"), new CustomerDtoMapper());
    }

    @Override
    public Integer countDtoWithSanitizeNeeded() {
        return jdbcTemplate.queryForObject(Queries.getQuery(QUERIES_FILE, "COUNT_CUSTOMERS_DTO_WITH_SANITIZE_NEEDED"), Integer.class);
    }

    @Override
    public List<CustomerDto> getDtoWithSanitizeLate() {
        return jdbcTemplate.query(Queries.getQuery(QUERIES_FILE, "SELECT_CUSTOMERS_DTO_WITH_SANITIZE_LATE"), new CustomerDtoMapper());
    }

    @Override
    public Integer countDtoWithSanitizeLate() {
        return jdbcTemplate.queryForObject(Queries.getQuery(QUERIES_FILE, "COUNT_CUSTOMERS_DTO_WITH_SANITIZE_LATE"), Integer.class);
    }

    @Override
    public List<CustomerDto> getDtoWithPackagingDebt() {
        return jdbcTemplate.query(Queries.getQuery(QUERIES_FILE, "SELECT_CUSTOMERS_DTO_WITH_PACKAGING_DEBT"), new CustomerDtoMapper());
    }

    @Override
    public Integer countDtoWithPackagingDebt() {
        return jdbcTemplate.queryForObject(Queries.getQuery(QUERIES_FILE, "COUNT_CUSTOMERS_DTO_WITH_PACKAGING_DEBT"), Integer.class);
    }

    @Override
    public List<CustomerDto> getDtoWithDebt() {
        return jdbcTemplate.query(Queries.getQuery(QUERIES_FILE, "SELECT_CUSTOMERS_DTO_WITH_DEBT"), new CustomerDtoMapper());
    }

    @Override
    public Integer countDtoWithDebt() {
        return jdbcTemplate.queryForObject(Queries.getQuery(QUERIES_FILE, "COUNT_CUSTOMERS_DTO_WITH_DEBT"), Integer.class);
    }

    @Override
    public List<LocalDate> selectDatesWithUnfulfilledObligationForCustomer(CustomerDto customerDto) {
        return jdbcTemplate.queryForList(Queries.getQuery(QUERIES_FILE, "COUNT_DATES_WITH_UNFULFILLED_OBLIGATION_WITH_BY_CUSTOMER_ID"), LocalDate.class, customerDto.getId(), customerDto.getId());
    }

    @Override
    public List<String> findDistinctCities(String text) {
        return jdbcTemplate.query(Queries.getQuery(QUERIES_FILE, "SELECT_DISTINCT_CITIES_BY_NAME"), (rs, rowNum) -> rs.getString(1), "%" + text + "%");
    }

    @Override
    public List<String> findDistinctCities() {
        return jdbcTemplate.query(Queries.getQuery(QUERIES_FILE, "SELECT_ALL_DISTINCT_CITIES_BY_NAME"), (rs, rowNum) -> rs.getString(1));
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
                    rs.getDate("date") != null ? rs.getDate("date").toLocalDate() : null,
                    Customer.LegalForm.valueOf(rs.getString("legal_form")),
                    rs.getInt("sanitise_period"),
                    rs.getDouble("debt"),
                    rs.getInt("packaging_small"),
                    rs.getInt("packaging_large"),
                    rs.getTimestamp("created_on") != null ? rs.getTimestamp("created_on").toLocalDateTime() : null,
                    rs.getTimestamp("modified_on") != null ? rs.getTimestamp("modified_on").toLocalDateTime() : null
            );
        }

        static Customer mapCustomer(ResultSet rs, int i) throws SQLException {
            CustomerRepositoryImpl.CustomerMapper cm = new CustomerRepositoryImpl.CustomerMapper();
            Customer customer;
            try {
                customer = cm.mapRow(rs, i);
            } catch (PSQLException e) {
                log.info("Exception mapping customer from db. Empty customer is returned. " + e.getMessage());
                customer = new Customer();
            }
            return customer;
        }
    }

    static class CustomerDtoMapper implements RowMapper<CustomerDto> {

        @Override
        public CustomerDto mapRow(ResultSet rs, int i) throws SQLException {
            return new CustomerDto(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("city"),
                    rs.getString("address"),
                    rs.getString("phone"),
                    rs.getDate("date") != null ? rs.getDate("date").toLocalDate() : null,
                    Customer.LegalForm.valueOf(rs.getString("legal_form")),
                    rs.getDate("sanitize_date") != null ? rs.getInt("months_without_fulfilled_monthly_obligation") : null,
                    rs.getInt("sanitise_period"),
                    rs.getDate("sanitize_date") != null ? rs.getDate("sanitize_date").toLocalDate() : null,
                    rs.getObject("month_until_sanitize") != null ? rs.getInt("month_until_sanitize") : null,
                    rs.getDouble("debt"),
                    rs.getInt("packaging_small"),
                    rs.getInt("packaging_large")
            );
        }
    }

}
