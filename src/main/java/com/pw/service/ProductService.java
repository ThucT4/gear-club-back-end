package com.pw.service;
 
import com.pw.model.Product;
import com.pw.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
 
@Service
public class ProductService extends CrudService<Product> {
 
    @Autowired
    private ProductRepository productRepository;

    // Create new product
    @Override
    public Product create(Product product) {
        return productRepository.save(product);
    }
 
    // Get the product by ID
    @Override
    public Product retrieve(int id) {
        return productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Product with id: " + id + ", not available."));
    }
 
    // Update product by ID
    @Override
    public Product update(Product product) {
        Product productDb = productRepository.findById(product.getId()).orElseThrow(
                () -> new EntityNotFoundException("Product with id: " + product.getId() + ", not available."));
 
        productDb.setName(product.getName());

        productDb.setImages(product.getImages());

        productDb.setVendorName(product.getVendorName());
        // productDb.setVendorImage(product.getVendorImage());

        // productDb.setDesignLocation(product.getDesignLocation());
        productDb.setFeatures(product.getFeatures());

        productDb.setIntro(product.getIntro());

        productDb.setPrice(product.getPrice());

        productDb.setWarranty(product.getWarranty());

        // productDb.setVariants(product.getVariants());
 
        return productRepository.save(productDb);
    }
 
    // Remove product by ID
    @Override
    public void delete(int id) {
        productRepository.deleteById(id);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> getFirst10() {
        return productRepository.findAll().stream().limit(10).collect(Collectors.toList()) ;

    }

    public List<Product> getSecond10() {
        List<Product> all = productRepository.findAll();

        if (all.size() > 10) {
            if (all.size() < 20) {
                return all.subList(10, all.size()-1);
            }
            else {
                return all.subList(10, 19);
            }
        }
        else {
            throw new EntityNotFoundException("There are no more products.");
        }
    }

    public List<Product> getByVendorName(String Vname) {
        return productRepository.findAll().stream()
            .filter(p -> p.getVendorName().toLowerCase().contains(Vname)).collect(Collectors.toList());
    }
}