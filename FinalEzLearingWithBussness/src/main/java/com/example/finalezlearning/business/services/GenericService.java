package com.example.finalezlearning.business.services;
import java.util.List;

public interface GenericService<T, K> {

    void create(T t);

    void update(T t);

    void delete(T t);

    List<T> getAll();

    T findById(K k);

}
