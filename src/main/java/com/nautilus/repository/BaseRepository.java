package com.nautilus.repository;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T, ID> {
    T insert(T obj);

    List<T> getAll();

    T findById(ID id);

    Optional<T> update(T obj);

    void deleteById(T obj);

    void deleteAll(Iterable<T> obj);
}
