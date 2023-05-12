package com.pw.controller;

import com.pw.model.Customer;
import com.pw.service.AdminService;
import com.pw.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("api/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    AdminService adminService;

    @GetMapping(value = "/all-cart")
    public ResponseEntity<List<HashMap<Object, Object>>> getAllCart() {
        return adminService.getAllCart();
    }

    @GetMapping(value = "/cart")
    public ResponseEntity<HashMap<Object, Object>> getCartByCustomerIdAndCartPosition(@RequestParam(name = "customerId") Integer customerId, @RequestParam(name = "cartPosition") int cartPosition) {
        return adminService.getCartByCustomerIdAndCartPosition(customerId, cartPosition);
    }

    @PutMapping(value = "/cart/accept-cart", consumes = "application/json")
    public ResponseEntity<HashMap<String, String>> acceptCart(@RequestBody HashMap<String, Integer> body) {
        return adminService.acceptCart(body.get("customerId"), body.get("cartPosition"));
    }

    @PutMapping(value = "/cart/decline-cart", consumes = "application/json")
    public ResponseEntity<HashMap<String, String>> declineCart(@RequestBody HashMap<String, Integer> body) {
        return adminService.declineCart(body.get("customerId"), body.get("cartPosition"));
    }

    @GetMapping(value = "/all-customers")
    public List<Customer> getAllCustomers() {
        return adminService.getAllCustomer();
    }

    @PutMapping(value = "/update-customer", consumes = "application/json", produces = "application/json")
    public Customer updateCustomer(@RequestBody Customer customer) {
        return adminService.updateCustomer(customer);
    }
}
