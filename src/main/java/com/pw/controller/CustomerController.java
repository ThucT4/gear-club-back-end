package com.pw.controller;

import com.pw.model.Customer;
import com.pw.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @PutMapping(value = "/cart/additem/", consumes = "application/json")
    public String addItem(@RequestBody Map<String, Integer> productForm) {
        return customerService.serviceAddToCart(productForm.get("customerID"), productForm.get("productID"), productForm.get("quantity"));
    }

    @PutMapping(value = "/cart/removeitem/", consumes = "application/json")
    public String removeItem(@RequestBody Map<String, Integer> productForm) {
        return customerService.serviceRemoveFromCart(productForm.get("customerID"), productForm.get("productID"));
    }

    @PutMapping(value = "/cart/increaseqty/", consumes = "application/json")
    public String increaseQuantity(@RequestBody Map<String, Integer> productForm) {
        return customerService.increaseQuantityCart(productForm.get("customerID"), productForm.get("productID"));
    }

    @PutMapping(value = "/cart/reduceqty/", consumes = "application/json")
    public String decreaseQuantity(@RequestBody Map<String, Integer> productForm) {
        return customerService.decreaseQuantityCart(productForm.get("customerID"), productForm.get("productID"));
    }

}
