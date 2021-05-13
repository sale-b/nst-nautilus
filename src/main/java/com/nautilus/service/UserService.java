package com.nautilus.service;

import com.nautilus.domain.User;

public interface UserService extends BaseService <User, Long>
{
    User findByEmailAndPass(String email, String pass);
    void deleteInBatch(Iterable<User> users);
}