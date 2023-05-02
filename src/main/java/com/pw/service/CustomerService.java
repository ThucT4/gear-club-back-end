package com.pw.service;

import com.pw.model.Customer;
import com.pw.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class CustomerService extends CrudService<Customer> {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductService productCrudService;

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public Customer retrieve(int id) {
        return customerRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Customer with id: "+ id + " not found"));
    }

    public Customer create(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer update(Customer customerDetails) {
        Customer customer = customerRepository.findById(customerDetails.getId()).orElseThrow(
                () -> new EntityNotFoundException("Customer with id: "+ customerDetails.getId() + " not found"));
        customer.setUsername(customerDetails.getUsername());
        customer.setPassword(customerDetails.getPassword());
        customer.setFirstName(customerDetails.getFirstName());
        customer.setLastName(customerDetails.getLastName());
        customer.setEmail(customerDetails.getEmail());
        customer.setPhone(customerDetails.getPhone());
        customer.setShippingAddress(customerDetails.getShippingAddress());
        customer.setShoppingCart(customerDetails.getShoppingCart());
        return customerRepository.save(customer);
    }

    public void delete(int id) {
        customerRepository.deleteById(id);
    }

    public void addToCart(int productId, int quantity) {

    }
}
