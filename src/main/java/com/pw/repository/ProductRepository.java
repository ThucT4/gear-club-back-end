package com.pw.repository;
 
import com.pw.model.Product;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
 
@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {
    
}