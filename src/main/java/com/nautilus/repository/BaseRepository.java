/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nautilus.repository;

import java.util.List;

/**
 *
 * @author laptop-02
 * @param <T>
 */
public abstract interface BaseRepository<T, ID> {
    T add (T obj);
    List<T> getAll();
    T findById(ID id);
    T update (T obj);
    void deleteById(T obj);
}
