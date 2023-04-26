package com.pw.service;

import java.util.List;

public class CrudService<T> {
 
    T create(T t) {
        return t;
    }
 
    T retrieve(int id) {
        return null;
    }
 
    T update(T t) {
        return null;
    }
 
    void delete(int id) {

    }

    List<T> findAll() {
        return null;
    }

}