package com.pw.service;
 
import com.pw.model.Collection;
import com.pw.model.Product;
import com.pw.repository.CollectionRepository;
import com.pw.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
 
@Service
public class CollectionService extends CrudService<Collection> {
    @Autowired
    private CollectionRepository collectionRepository;

    // Create new product
    @Override
    public Collection create(Collection collection) {
        return collectionRepository.save(collection);
    }
 
    // Get the product by ID
    @Override
    public Collection retrieve(int id) {
        return collectionRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Product with id: " + id + ", not available."));
    }

    public Collection getByName(String name) {
        List<Collection> list = collectionRepository.findAll().stream().filter(c -> c.getName().equals(name)).collect(Collectors.toList());

        return list.size() > 0 ? list.get(0) : null;
    }
 
    // Update product by ID
    @Override
    public Collection update(Collection newCollection) {
        Collection oldCollection = collectionRepository.findById(newCollection.getId()).orElseThrow(
                () -> new EntityNotFoundException("Product with id: " + newCollection.getId() + ", not available."));
 
                oldCollection.setName(newCollection.getName());
                oldCollection.setProductList(newCollection.getProductList());
 
        return collectionRepository.save(oldCollection);
    }
 
    // Remove product by ID
    @Override
    public void delete(int id) {
        collectionRepository.deleteById(id);
    }

    public List<Collection> findAll() {
        return collectionRepository.findAll();
        
    }

    
}
