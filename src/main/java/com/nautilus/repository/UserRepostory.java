package com.nautilus.repository;

import com.nautilus.domain.User;

public interface UserRepostory extends BaseRepository<User, Long>
{
    User findByEmailAndPass(String email, String pass);
    void deleteInBatch (Iterable<User> users);
}
