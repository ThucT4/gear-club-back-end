package com.pw.service;

import com.pw.model.Customer;
import com.pw.model.Product;
import com.pw.repository.CustomerRepository;
import com.pw.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CustomerService extends CrudService<Customer> {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductService productCrudService;

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Customer retrieve(int id) {
        return customerRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Customer with id: "+ id + " not found"));
    }

    @Override
    public Customer create(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Customer update(Customer customerDetails) {
        Customer customer = customerRepository.findById(customerDetails.getId()).orElseThrow(
                () -> new EntityNotFoundException("Customer with id: "+ customerDetails.getId() + " not found"));
        customer.setUsername(customerDetails.getUsername());
        customer.setPassword(customerDetails.getPassword());
        customer.setName(customerDetails.getName());
        customer.setEmail(customerDetails.getEmail());
        customer.setPhone(customerDetails.getPhone());
        customer.setShippingAddress(customerDetails.getShippingAddress());
        customer.setShoppingCart(customerDetails.getShoppingCart());
        return customerRepository.save(customer);
    }

    @Override
    public void delete(int id) {
        customerRepository.deleteById(id);
    }

    public HashMap<Integer, Integer> retrieveLatestCart(int customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new EntityNotFoundException("Customer with id: " + customerId + " not found"));
        List<HashMap<Integer, Integer>> shoppingCart = customer.getShoppingCart();
        return customer.getShoppingCart().get(shoppingCart.size() - 1);
    }

    public List<HashMap<Integer, Integer>> findAllCart(int customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new EntityNotFoundException("Customer with id: " + customerId + " not found"));
        return customer.getShoppingCart();
    }

    public int getTotalPrice(HashMap<Integer, Integer> shoppingCart) {
        int totalPrice = 0;
        for(Map.Entry<Integer,Integer> cart : shoppingCart.entrySet()) {
            Product product = productCrudService.retrieve(cart.getKey());
            totalPrice = totalPrice + (product.getPrice().intValue() * cart.getValue());
        }
        return totalPrice;
    }

    public boolean isItemInCart(int customerId, int productId){
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new EntityNotFoundException("Customer with id: "+ customerId + " not found"));
        List<HashMap<Integer, Integer>> shoppingCart = customer.getShoppingCart();
        return shoppingCart.get(shoppingCart.size() - 1).containsKey(productId);
    }

    public void addItemToCart(int customerId, int productId, int quantity) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new EntityNotFoundException("Customer with id: "+ customerId + " not found"));
        List<HashMap<Integer, Integer>> shoppingCart = customer.getShoppingCart();
        customer.getShoppingCart().get(shoppingCart.size() - 1).put(productId,quantity);
        update(customer);
    }

    public void removeItemFromCart(int customerId, int productId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new EntityNotFoundException("Customer with id: "+ customerId + " not found"));
        List<HashMap<Integer, Integer>> shoppingCart = customer.getShoppingCart();
        customer.getShoppingCart().get(shoppingCart.size() - 1).remove(productId);
        update(customer);
    }


    public String serviceAddToCart(int customerId, int productId, int quantity) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new EntityNotFoundException("Customer with id: "+ customerId + " not found"));
        Product product = productCrudService.retrieve(productId);

        if(isItemInCart(customerId,productId)) {
            return "duplicated";
        } else if(quantity >= product.getQuantity()) {
            return "database_error";
        } else {
            addItemToCart(customerId,productId,quantity);
            return "success";
        }
    }

    public String serviceRemoveFromCart(int customerId, int productId) {
        if(!isItemInCart(customerId,productId)) {
            return "item_not_found";
        } else {
            removeItemFromCart(customerId,productId);
            return "success";
        }
    }

    public String increaseQuantityCart(int customerId, int productId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new EntityNotFoundException("Customer with id: "+ customerId + " not found"));
        Product product = productCrudService.retrieve(productId);
        List<HashMap<Integer, Integer>> shoppingCart = customer.getShoppingCart();
        HashMap<Integer, Integer> latestCart = shoppingCart.get(shoppingCart.size() - 1);

        if(latestCart.get(productId) + 1 > product.getQuantity()){
            return "database_error";
        } else {
            customer.getShoppingCart().get(shoppingCart.size() - 1).put(productId,latestCart.get(productId)+1);
            update(customer);
            return "success";
        }
    }

    public String decreaseQuantityCart(int customerId, int productId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new EntityNotFoundException("Customer with id: "+ customerId + " not found"));
        Product product = productCrudService.retrieve(productId);
        List<HashMap<Integer, Integer>> shoppingCart = customer.getShoppingCart();
        HashMap<Integer, Integer> latestCart = shoppingCart.get(shoppingCart.size() - 1);

        if(latestCart.get(productId) - 1 <= 0){
            return "database_error";
        } else {
            customer.getShoppingCart().get(shoppingCart.size() - 1).put(productId,latestCart.get(productId)-1);
            update(customer);
            return "success";
        }
    }
}
