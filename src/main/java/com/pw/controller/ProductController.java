package com.pw.controller;
 
import com.pw.model.Product;
import com.pw.service.CrudService;
import com.pw.service.ProductService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
 
@RestController
@RequestMapping("api/product")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProductController {
 
    @Autowired
    private ProductService productCrudService;
 
    @PostMapping(value = "/", consumes = "application/json")
    public Product create(@RequestBody Product product) {
        return productCrudService.create(product);
    }
 
    @GetMapping(value = "/{id}", produces = "application/json")
    public Product retrieve(@PathVariable int id) {
        return productCrudService.retrieve(id);
    }
 
    @PutMapping(value = "/", consumes = "application/json")
    public Product update(@RequestBody Product product) {
        return productCrudService.update(product);
    }
 
    @DeleteMapping(value = "/")
    public String delete(int id) {
        productCrudService.delete(id);
        return "Done";
    }

    @GetMapping(value = "/all", produces = "application/json")
    public List<Product> getAll() {
        return productCrudService.findAll();
    }

    @GetMapping(value = "/first10", produces = "application/json")
    public List<Product> getFirst10() {
        return productCrudService.getFirst10();
    }

    @GetMapping(value = "/second10", produces = "application/json")
    public List<Product> getSecond10() {
        return productCrudService.getSecond10();
    }

    @GetMapping(value = "/vendor/{name}", produces = "application/json")
    public List<Product> getSecond10(@PathVariable String name) {
        return productCrudService.getByVendorName(name);
    }
}