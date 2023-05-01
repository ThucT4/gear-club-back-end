package com.pw.repository;
 
import com.pw.model.Collection;
import com.pw.model.Product;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
 
@Repository
public interface CollectionRepository extends PagingAndSortingRepository<Collection, Integer> {
    List<Collection> findAll();
}