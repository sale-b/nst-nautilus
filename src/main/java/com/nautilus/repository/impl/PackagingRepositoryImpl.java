package com.nautilus.repository.impl;

import com.nautilus.domain.Customer;
import com.nautilus.domain.Packaging;
import com.nautilus.repository.PackagingRepository;
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
public class PackagingRepositoryImpl implements PackagingRepository {

    private final String QUERIES_FILE = "dbqueries/packaging-queries.properties";


    private final JdbcTemplate jdbcTemplate;

    public PackagingRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Packaging insert(Packaging packaging) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(Queries.getQuery(QUERIES_FILE,"INSERT_PACKAGING"), Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, packaging.getCustomer().getId());
            ps.setDate(2,  Date.valueOf(packaging.getDate()));
            ps.setInt(3, packaging.getWaterSmallReturned());
            ps.setInt(4, packaging.getWaterLargeReturned());
            return ps;
        }, keyHolder);

        packaging.setId((Long) (Objects.requireNonNull(keyHolder.getKeys()).get("id")));
        packaging.setCreatedOn(((Timestamp) Objects.requireNonNull(keyHolder.getKeys()).get("created_on")).toLocalDateTime());
        packaging.setModifiedOn(((Timestamp) Objects.requireNonNull(keyHolder.getKeys()).get("modified_on")).toLocalDateTime());
        return packaging;
    }

    @Transactional
    @Override
    public List<Packaging> getAll() {
        return jdbcTemplate.query(Queries.getQuery(QUERIES_FILE,"SELECT_ALL_PACKAGING"), new PackagingMapper());
    }

    @Transactional
    @Override
    public Packaging findById(Long id) {
        return jdbcTemplate.queryForObject(Queries.getQuery(QUERIES_FILE,"SELECT_PACKAGING_BY_ID"), new PackagingMapper(), id);
    }

    @Transactional
    @Override
    public Optional<Packaging> update(Packaging packaging) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(Queries.getQuery(QUERIES_FILE,"UPDATE_PACKAGING_BY_ID"), Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, packaging.getCustomer().getId());
            ps.setDate(2,  Date.valueOf(packaging.getDate()));
            ps.setInt(3, packaging.getWaterSmallReturned());
            ps.setInt(4, packaging.getWaterLargeReturned());
            ps.setLong(5, packaging.getId());
            ps.setTimestamp(6, Timestamp.valueOf(packaging.getModifiedOn()));
            return ps;
        }, keyHolder);
        if (keyHolder.getKeys() == null) {
            return Optional.empty();
        } else {
            packaging.setModifiedOn(((Timestamp) Objects.requireNonNull(keyHolder.getKeys()).get("modified_on")).toLocalDateTime());
        }
        return Optional.of(packaging);
    }

    @Transactional
    @Override
    public void deleteById(Packaging packaging) {
        jdbcTemplate.update(Queries.getQuery(QUERIES_FILE,"DELETE_PACKAGING_BY_ID"),
                packaging.getId());
    }


    @Transactional
    @Override
    public void deleteAll(Iterable<Packaging> packagings) {
        packagings.forEach(this::deleteById);
    }

    static class PackagingMapper implements RowMapper<Packaging> {

        @Override
        public Packaging mapRow(@NonNull ResultSet rs, int i) throws SQLException {
            Customer customer = CustomerRepositoryImpl.CustomerMapper.mapCustomer(rs, i);
            return new Packaging(
                    rs.getLong("packaging_id"),
                    customer,
                    rs.getDate("packaging_date").toLocalDate(),
                    rs.getInt("water_small_returned"),
                    rs.getInt("water_large_returned"),
                    rs.getTimestamp("packaging_created_on") != null ? rs.getTimestamp("packaging_created_on").toLocalDateTime() : null,
                    rs.getTimestamp("packaging_modified_on") != null ? rs.getTimestamp("packaging_modified_on").toLocalDateTime() : null
            );
        }

    }
}
