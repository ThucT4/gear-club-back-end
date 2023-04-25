package com.pw.service;
 
import com.pw.model.Product;
import com.pw.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import javax.persistence.EntityNotFoundException;
 
@Service
public class ProductService implements CrudService<Product> {
 
    @Autowired
    private ProductRepository productRepository;
 
    @Override
    public Product create(Product product) {
        return productRepository.save(product);
    }
 
    @Override
    public Product retrieve(int id) {
        return productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Product with id: " + id + ", not available."));
    }
 
    @Override
    public Product update(Product product) {
        Product productDb = productRepository.findById(product.getId()).orElseThrow(
                () -> new EntityNotFoundException("Product with id: " + product.getId() + ", not available."));
 
        productDb.setName(product.getName());
        // productDb.setImages(product.getImages());

        // productDb.setVendorName(product.getVendorName());
        // productDb.setVendorImage(product.getVendorImage());

        // productDb.setDesignLocation(product.getDesignLocation());
        // // productDb.setFeatures(product.getFeatures());
        // productDb.setIntro(product.getIntro());
        // productDb.setPrice(product.getPrice());
        // productDb.setWarranty(product.getWarranty());
        // productDb.setVariants(product.getVariants());

 
        return productRepository.save(productDb);
    }
 
    @Override
    public void delete(int id) {
        productRepository.deleteById(id);
    }
}