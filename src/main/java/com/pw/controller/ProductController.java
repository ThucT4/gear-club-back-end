package com.pw.controller;
 
import com.pw.model.Product;
import com.pw.service.ProductService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
 
@RestController
@RequestMapping("api/product")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class ProductController {
 
    @Autowired
    private ProductService productCrudService;

    @PutMapping(value = "/search-by-string")
    public List<Product> searchByString(@RequestBody Map<String, String> searchRequest) {
        return productCrudService.searchByString(searchRequest);
    }
 
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
 
    @DeleteMapping(value = "/{id}")
    public String delete(@PathVariable int id) {
        productCrudService.delete(id);
        return "Done";
    }

    @GetMapping(value = "/all", produces = "application/json")
    public List<Product> getAll() {
        return productCrudService.findAll();
    }

    @GetMapping(value = "/filter/{query}", produces = "application/json")
    public JSONObject getSecond10(@PathVariable String query) {
        return productCrudService.getCustom(query);
    }
}