package com.nautilus.service.impl;

import com.nautilus.domain.User;
import com.nautilus.repository.UserRepostory;
import com.nautilus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepostory userRepostory;

    @Override
    public User add(User obj) {
        return userRepostory.add(obj);
    }

    @Override
    public List<User> getAll() {
        return userRepostory.getAll();
    }

    @Override
    public User findById(Long id) {
        return userRepostory.findById(id);
    }

    @Override
    public User update(User user) {
        return userRepostory.update(user);
    }

    @Override
    public User findByEmailAndPass(String email, String pass) {
        return userRepostory.findByEmailAndPass(email, pass);
    }

    @Override
    public void deleteInBatch(Iterable<User> users) {
        userRepostory.deleteInBatch(users);
    }
}
