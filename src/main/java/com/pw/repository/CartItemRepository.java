package com.pw.repository;

import com.pw.model.CartItem;
import com.pw.model.ShoppingCart;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends CrudRepository<CartItem, Integer> {
    List<CartItem> findAll();
    List<CartItem> findAllByShoppingCart(ShoppingCart shoppingCart);
}