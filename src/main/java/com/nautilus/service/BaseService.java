package com.nautilus.service;

import java.util.List;
import java.util.Optional;

public interface BaseService<T, ID> {
    T insert(T obj);

    List<T> getAll();

    T findById(ID id);

    Optional<T> update(T obj);

    void deleteAll(Iterable<T> obj);
}