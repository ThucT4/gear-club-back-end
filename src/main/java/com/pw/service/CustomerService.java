package com.pw.service;

import com.pw.model.Customer;
import com.pw.model.HttpResponse;
import com.pw.model.Product;
import com.pw.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
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

        customer.setEmail(customerDetails.getEmail());
        customer.setPassword(customerDetails.getPassword());
        customer.setFirstName(customerDetails.getFirstName());
        customer.setLastName(customerDetails.getLastName());
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
            if(cart.getKey() < 0) {
                break;
            }
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


    public HttpResponse serviceAddToCart(int customerId, int productId, int quantity) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new EntityNotFoundException("Customer with id: "+ customerId + " not found"));
        Product product = productCrudService.retrieve(productId);

        if(isItemInCart(customerId,productId)) {
            return HttpResponse.builder().status("bad").message("duplicated").build();
        } else if(quantity >= product.getQuantity()) {
            return HttpResponse.builder().status("bad").message("database_error").build();
        } else {
//            addItemToCart(customerId,productId,quantity);

            ArrayList<HashMap<Integer, Integer>> cartList = (ArrayList<HashMap<Integer, Integer>>) customer.getShoppingCart().clone();
            HashMap<Integer, Integer> currentCart = cartList.get(cartList.size() - 1);
            currentCart.put(productId, quantity);
            customer.setShoppingCart(cartList);
            customerRepository.save(customer);

            return HttpResponse.builder().status("ok").message("success").build();
        }
    }

    public HttpResponse serviceRemoveFromCart(int customerId, int productId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new EntityNotFoundException("Customer with id: "+ customerId + " not found"));
        Product product = productCrudService.retrieve(productId);

        if(!isItemInCart(customerId,productId)) {
            return HttpResponse.builder().status("bad").message("item_not_found").build();
        } else {
//            removeItemFromCart(customerId,productId);

            ArrayList<HashMap<Integer, Integer>> cartList = (ArrayList<HashMap<Integer, Integer>>) customer.getShoppingCart().clone();
            HashMap<Integer, Integer> currentCart = cartList.get(cartList.size() - 1);
            currentCart.remove(productId);
            customer.setShoppingCart(cartList);
            customerRepository.save(customer);

            return HttpResponse.builder().status("ok").message("success").build();
        }
    }

    public HttpResponse increaseQuantityCart(int customerId, int productId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new EntityNotFoundException("Customer with id: "+ customerId + " not found"));
        Product product = productCrudService.retrieve(productId);
        List<HashMap<Integer, Integer>> shoppingCart = customer.getShoppingCart();
        HashMap<Integer, Integer> latestCart = shoppingCart.get(shoppingCart.size() - 1);

        if(latestCart.get(productId) + 1 > product.getQuantity()){
            return HttpResponse.builder().status("bad").message("database_error").build();
        } else {
//            customer.getShoppingCart().get(shoppingCart.size() - 1).put(productId,latestCart.get(productId)+1);
//            update(customer);

            ArrayList<HashMap<Integer, Integer>> cartList = (ArrayList<HashMap<Integer, Integer>>) customer.getShoppingCart().clone();
            HashMap<Integer, Integer> currentCart = cartList.get(cartList.size() - 1);
            currentCart.put(productId, currentCart.get(productId) + 1);
            customer.setShoppingCart(cartList);
            customerRepository.save(customer);

            return HttpResponse.builder().status("ok").message("success").build();
        }
    }

    public HttpResponse decreaseQuantityCart(int customerId, int productId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new EntityNotFoundException("Customer with id: "+ customerId + " not found"));
        Product product = productCrudService.retrieve(productId);
        List<HashMap<Integer, Integer>> shoppingCart = customer.getShoppingCart();
        HashMap<Integer, Integer> latestCart = shoppingCart.get(shoppingCart.size() - 1);

        if(latestCart.get(productId) - 1 <= 0){
            return HttpResponse.builder().status("bad").message("database_error").build();
        } else {
//            customer.getShoppingCart().get(shoppingCart.size() - 1).put(productId,latestCart.get(productId)-1);
//            update(customer);

            ArrayList<HashMap<Integer, Integer>> cartList = (ArrayList<HashMap<Integer, Integer>>) customer.getShoppingCart().clone();
            HashMap<Integer, Integer> currentCart = cartList.get(cartList.size() - 1);
            currentCart.put(productId, currentCart.get(productId) - 1);
            customer.setShoppingCart(cartList);
            customerRepository.save(customer);

            return HttpResponse.builder().status("ok").message("success").build();
        }
    }

//        for(Map.Entry<Integer,Integer> cart : latestCart.entrySet()) {
//            Product product = productCrudService.retrieve(cart.getKey());
//            if(cart.getValue() > product.getQuantity()) {
//                return "not enough quantity: " + cart.getKey();
//            } else {
//                product.setQuantity(product.getQuantity() - cart.getValue());
//                productCrudService.update(product);
//            }
//        }
//        Long unixTime = Instant.now().getEpochSecond();
//        latestCart.put(-1, 2);
//        latestCart.put(-2, unixTime.intValue());
//        latestCart.put(-3, getTotalPrice(latestCart));
//        HashMap<Integer, Integer> newCart = new HashMap<>();
//        customer.getShoppingCart().add(newCart);
//        update(customer);
//        return "success";
//    }
}
