package com.pw.controller;
 
import com.pw.model.Product;
import com.pw.service.CrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
 
@RestController
@RequestMapping("api/product")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProductController {
 
    @Autowired
    private CrudService<Product> productCrudService;
 
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
}