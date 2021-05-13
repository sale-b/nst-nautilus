/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nautilus.repository.impl;

import com.nautilus.domain.User;
import com.nautilus.repository.UserRepostory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.sql.*;
import java.util.List;
import java.util.Optional;

/**
 * @author laptop-02
 */
@Repository
@Transactional
public class UserRepositoryImpl implements UserRepostory {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public User add(User user) {

//        jdbcTemplate.update("INSERT into User (dob, first_name, last_name, email, password, gender, role) VALUES (?,?,?,?,?,?,?)",
//                Timestamp.valueOf(user.getDob().atStartOfDay()),
//                user.getFirstName(),
//                user.getLastName(),
//                user.getEmail(),
//                user.getPassword(),
//                user.getGender(),
//                user.getRole());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement("INSERT into User (dob, first_name, last_name, email, password, gender, role) VALUES (?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setTimestamp(1, Timestamp.valueOf(user.getDob().atStartOfDay()));
            ps.setString(2, user.getFirstName());
            ps.setString(3, user.getLastName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPassword());
            ps.setString(6, user.getGender());
            ps.setString(7, user.getRole());
            return ps;
        }, keyHolder);

        user.setId((keyHolder.getKey()).longValue());
        return user;
    }

    @Transactional
    @Override
    public List<User> getAll() {
        String query = "SELECT * from User";
        List<User> users = jdbcTemplate.query(query, new UserMapper());
        return users;
    }

    @Transactional
    @Override
    public User findById(Long id) {
        String query = "select * from User where id = ?";
        return jdbcTemplate.queryForObject(query, new UserMapper(), new Object[]{id});
    }

    @Transactional
    @Override
    public User update(User user) {
        String query = "UPDATE User\n" +
                "SET dob=?, email=?, first_name=?, gender=?, last_name=?, password=?, `role`=?\n" +
                "WHERE id=?;\n";
        jdbcTemplate.update(query,
                Timestamp.valueOf(user.getDob().atStartOfDay()),
                user.getEmail(),
                user.getFirstName(),
                user.getGender(),
                user.getLastName(),
                user.getPassword(),
                user.getRole(),
                user.getId());
        return user;
    }

    @Transactional
    @Override
    public void deleteById(User user) {
        String query = "DELETE FROM User\n" +
                "WHERE id=?;\n";
        jdbcTemplate.update(query,
                user.getId());
    }

    @Transactional
    @Override
    public User findByEmailAndPass(String email, String pass) {
        String query = "select * from User where email = ? and password = ?";
        User u = null;
        try {
            u = jdbcTemplate.queryForObject(query, new UserMapper(), new Object[]{email, pass});
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return u;
    }

    @Transactional
    @Override
    public void deleteInBatch(Iterable<User> users) {
        users.forEach(user -> deleteById(user));
    }

    class UserMapper implements RowMapper<User> {

        @Override
        public User mapRow(ResultSet rs, int i) throws SQLException {
            Timestamp dob = rs.getTimestamp("dob");
            return new User(
                    rs.getLong("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    dob != null ? dob.toLocalDateTime().toLocalDate() : null,
                    rs.getString("gender"),
                    rs.getString("role")
            );
        }

    }
}
