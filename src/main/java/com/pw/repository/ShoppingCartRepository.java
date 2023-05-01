package com.pw.repository;

import com.pw.model.Customer;
import com.pw.model.ShoppingCart;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, Integer> {
    List<ShoppingCart> findAll();
    Optional<ShoppingCart> findByCustomer(Customer customer);
}