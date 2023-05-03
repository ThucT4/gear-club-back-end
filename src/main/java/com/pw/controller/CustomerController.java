package com.pw.controller;

import com.pw.model.Customer;
import com.pw.model.Product;
import com.pw.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/customer")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping(value = "/", consumes = "application/json")
    public Customer create(@RequestBody Customer customer) {
        return customerService.create(customer);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public Customer retrieve(@PathVariable int id) {
        return customerService.retrieve(id);
    }

    @PutMapping(value = "/", consumes = "application/json")
    public Customer update(@RequestBody Customer customer) {
        return customerService.update(customer);
    }

    @DeleteMapping(value = "/")
    public String delete(int id) {
        customerService.delete(id);
        return "Done";
    }


}
