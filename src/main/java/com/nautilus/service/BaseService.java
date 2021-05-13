package com.nautilus.service;

import java.util.List;

public interface BaseService <T, ID> {
    T add (T obj);
    List<T> getAll();
    T findById(ID id);
    T update (T obj);
}