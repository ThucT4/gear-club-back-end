package com.pw.controller;
 
import com.pw.model.Collection;
import com.pw.model.Product;
import com.pw.service.CollectionService;
import com.pw.service.CrudService;
import com.pw.service.ProductService;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
 
@RestController
@RequestMapping("api/collection")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CollectionController {
    @Autowired
    private CollectionService collectionService;

    @PostMapping(value = "/", consumes = "application/json")
    public Collection create(@RequestBody Collection collection) {
        return collectionService.create(collection);
    }
 
    @GetMapping(value = "/{name}", produces = "application/json")
    public Collection retrieve(@PathVariable String name) {
        return collectionService.getByName(name);
    }
 
    @PutMapping(value = "/", consumes = "application/json")
    public Collection update(@RequestBody Collection collection) {
        return collectionService.update(collection);
    }
 
    @DeleteMapping(value = "/{id}")
    public String delete(@PathVariable int id) {
        collectionService.delete(id);
        return "Done";
    }

    @GetMapping(value = "/all", produces = "application/json")
    public List<Collection> getAll() {
        return collectionService.findAll();
    }
}
