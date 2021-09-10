package com.nautilus.repository.impl;

import com.nautilus.domain.Customer;
import com.nautilus.domain.Sanitize;
import com.nautilus.repository.SanitizeRepository;
import com.nautilus.util.Queries;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.NonNull;
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
public class SanitizeRepositoryImpl implements SanitizeRepository {

    private final String QUERIES_FILE = "dbqueries/sanitize-queries.properties";


    private final JdbcTemplate jdbcTemplate;

    public SanitizeRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Sanitize insert(Sanitize sanitize) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(Queries.getQuery(QUERIES_FILE,"INSERT_SANITIZE"), Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, sanitize.getCustomer().getId());
            ps.setDate(2, Date.valueOf(sanitize.getDate()));
            return ps;
        }, keyHolder);

        sanitize.setId((Long) (Objects.requireNonNull(keyHolder.getKeys()).get("id")));
        return sanitize;
    }

    @Transactional
    @Override
    public List<Sanitize> getAll() {
        return jdbcTemplate.query(Queries.getQuery(QUERIES_FILE,"SELECT_ALL_SANITIZE"), new SanitizeMapper());
    }

    @Transactional
    @Override
    public Sanitize findById(Long id) {
        return jdbcTemplate.queryForObject(Queries.getQuery(QUERIES_FILE,"SELECT_SANITIZE_BY_ID"), new SanitizeMapper(), id);
    }

    @Transactional
    @Override
    public Optional<Sanitize> update(Sanitize sanitize) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(Queries.getQuery(QUERIES_FILE,"UPDATE_SANITIZE_BY_ID"), Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, sanitize.getCustomer().getId());
            ps.setDate(2, Date.valueOf(sanitize.getDate()));
            ps.setLong(3, sanitize.getId());
            ps.setTimestamp(4, Timestamp.valueOf(sanitize.getModifiedOn()));
            return ps;
        }, keyHolder);
        if (keyHolder.getKeys() == null)
            return Optional.empty();
        return Optional.of(sanitize);
    }

    @Transactional
    @Override
    public void deleteById(Sanitize sanitize) {
        jdbcTemplate.update(Queries.getQuery(QUERIES_FILE,"DELETE_SANITIZE_BY_ID"),
                sanitize.getId());
    }

    @Transactional
    @Override
    public void deleteAll(Iterable<Sanitize> sanitizes) {
        sanitizes.forEach(this::deleteById);
    }

    static class SanitizeMapper implements RowMapper<Sanitize> {

        @Override
        public Sanitize mapRow(@NonNull ResultSet rs, int i) throws SQLException {
            Customer customer = CustomerRepositoryImpl.CustomerMapper.mapCustomer(rs, i);
            return new Sanitize(
                    rs.getLong("sanitize_id"),
                    customer,
                    rs.getDate("sanitize_date").toLocalDate(),
                    rs.getTimestamp("sanitize_created_on") != null ? rs.getTimestamp("sanitize_created_on").toLocalDateTime() : null,
                    rs.getTimestamp("sanitize_modified_on") != null ? rs.getTimestamp("sanitize_modified_on").toLocalDateTime() : null
            );
        }

    }
}
