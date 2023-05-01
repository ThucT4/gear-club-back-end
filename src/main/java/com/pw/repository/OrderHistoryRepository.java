package com.pw.repository;

import com.pw.model.Customer;
import com.pw.model.OrderHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderHistoryRepository extends CrudRepository<OrderHistory, Integer> {
    List<OrderHistory> findAll();
    List<OrderHistory> findAllByCustomer(Customer customer);
}