package com.pw.service;
 
public interface CrudService<T> {
 
    T create(T t);
 
    T retrieve(int id);
 
    T update(T t);
 
    void delete(int id);
}