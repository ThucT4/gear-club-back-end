package com.pw.service;

import com.pw.mailsender.EmailService;
import com.pw.model.Customer;
import com.pw.model.Product;
import com.pw.model.Role;
import com.pw.model.Subscriber;
import com.pw.repository.CustomerRepository;
import com.pw.repository.ProductRepository;
import com.pw.repository.SubscriberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AdminService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    SubscriberRepository subscriberRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    private ProductService productCrudService;

    public ResponseEntity<List<HashMap<Object, Object>>> getAllCart() {
        List<Customer> customers = customerRepository.findAll();

        List<HashMap<Object, Object>> resBody = new ArrayList<>();

        for (Customer customer : customers) {


            // Traverse each cart
            for (HashMap<Integer, Integer> curCart : customer.getShoppingCart().subList(0, customer.getShoppingCart().size() - 1)) { // Get cart from 0 -> Last - 1 position (ignore the last)
                HashMap<Object, Object> resCart = new HashMap<>();
                ArrayList<HashMap<String, Object>> productList = new ArrayList<>();
                resCart.put("customerId", customer.getId());
                resCart.put("customerFirstName", customer.getFirstName());
                resCart.put("customerLastName", customer.getLastName());
                resCart.put("customerPhone", customer.getPhone());
                resCart.put("customerEmail", customer.getEmail());
                resCart.put("productList", productList);

                for (Map.Entry<Integer, Integer> entry : curCart.entrySet()) {
                    int key = entry.getKey();
                    int value = entry.getValue();

                    if (key == -1) {
                        resCart.put("cartPosition", value);
                    } else if (key == -2) {
                        resCart.put("cartStatus", value);
                    } else if (key == -3) {
                        resCart.put("cartPaymentTime", value);
                    } else if (key == -4) {
                        resCart.put("cartTotalPrice", value);
                    }  else {
                        Optional<Product> curOptionalProduct = productRepository.findById(key);

                        if (curOptionalProduct.isPresent()) {
                            Product curProduct = curOptionalProduct.get();

                            HashMap<String, Object> resProduct = new HashMap<String, Object>();
                            resProduct.put("name", curProduct.getName());
                            resProduct.put("image", curProduct.getImages().get(0));
                            resProduct.put("paymentQuantity", value);

                            productList.add(resProduct);
                        }
                    }
                }

                resBody.add(resCart);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    public ResponseEntity<HashMap<Object, Object>> getCartByCustomerIdAndCartPosition(Integer customerId, int cartPosition) {
        Optional<Customer> customerDB = customerRepository.findById(customerId);
        HashMap<Object, Object> resCart = new HashMap<>();
        ArrayList<HashMap<String, Object>> productList = new ArrayList<>();

        if (customerDB.isPresent()) {
            Customer customer = customerDB.get();

            resCart.put("customerId", customer.getId());
            resCart.put("customerAddress", customer.getShippingAddress());
            resCart.put("customerFirstName", customer.getFirstName());
            resCart.put("customerLastName", customer.getLastName());
            resCart.put("customerPhone", customer.getPhone());
            resCart.put("customerEmail", customer.getEmail());
            resCart.put("productList", productList);

            HashMap<Integer, Integer> curCart = customer.getShoppingCart().get(cartPosition);
            for (Map.Entry<Integer, Integer> entry : curCart.entrySet()) {
                int key = entry.getKey();
                int value = entry.getValue();

                if (key == -1) {
                    resCart.put("cartPosition", value);
                } else if (key == -2) {
                    resCart.put("cartStatus", value);
                } else if (key == -3) {
                    resCart.put("cartPaymentTime", value);
                } else if (key == -4) {
                    resCart.put("cartTotalPrice", value);
                }  else {
                    Optional<Product> curOptionalProduct = productRepository.findById(key);

                    if (curOptionalProduct.isPresent()) {
                        Product curProduct = curOptionalProduct.get();

                        HashMap<String, Object> resProduct = new HashMap<String, Object>();
                        resProduct.put("id", curProduct.getId());
                        resProduct.put("name", curProduct.getName());
                        resProduct.put("price", curProduct.getPrice());
                        resProduct.put("image", curProduct.getImages().get(0));
                        resProduct.put("paymentQuantity", value);

                        productList.add(resProduct);
                    }
                }
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(resCart);
    }

    public ResponseEntity<HashMap<String, String>> acceptCart(Integer customerId, int cartPosition) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            ArrayList<HashMap<Integer, Integer>> curCartList = (ArrayList<HashMap<Integer, Integer>>) customer.getShoppingCart().clone();

            curCartList.get(cartPosition).put(-2, 3); // Change status of cart to accepted
            customer.setShoppingCart(curCartList);

            customerRepository.save(customer);
        }

        HashMap<String, String> body = new HashMap<>();
        body.put("message", "cart_accepted");
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    public ResponseEntity<HashMap<String, String>> declineCart(Integer customerId, int cartPosition) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            ArrayList<HashMap<Integer, Integer>> curCartList = (ArrayList<HashMap<Integer, Integer>>) customer.getShoppingCart().clone();

            curCartList.get(cartPosition).put(-2, 4); // Change status of cart to declined
            customer.setShoppingCart(curCartList);

            customerRepository.save(customer);
        }

        HashMap<String, String> body = new HashMap<>();
        body.put("message", "cart_declined");
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    public List<Customer> getAllCustomer() {
        List<Customer> customers = customerRepository.findAll();

        List<Customer> customersNotAdmin = new ArrayList<>();

        for (Customer customer : customers) {
            if (customer.getRole() == Role.ADMIN) {
                continue;
            }

            customersNotAdmin.add(customer);
        }
        return customersNotAdmin;
    }

    public Customer updateCustomer(Customer customer) {
        Customer customerDB = customerRepository.findById(customer.getId()).orElseThrow(() -> {throw new EntityNotFoundException("User not found");});

        customerDB.setFirstName(customer.getFirstName());
        customerDB.setLastName(customer.getLastName());
        customerDB.setPhone(customer.getPhone());
        customerDB.setShippingAddress(customer.getShippingAddress());

        customerRepository.save(customerDB);

        return customerDB;
    }

    public ResponseEntity<HashMap<Object, Object>> sendEmailToAllSubscribers(String subject, String content) {
        try {
            List<Subscriber> subscribers = subscriberRepository.findAll();

            for (Subscriber subscriber : subscribers) {
                emailService.sendSimpleMessage(subscriber.getEmail(), subject, content);
            }

            HashMap<Object, Object> body = new HashMap<>();
            body.put("status", "mail_send_ok");
            return ResponseEntity.status(HttpStatus.OK).body(body);
        } catch (Exception e) {
            HashMap<Object, Object> body = new HashMap<>();
            body.put("status", "mail_send_failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    public ResponseEntity<HashMap<Object, Object>> sendEmailToOneSubscriber(Integer subscriberId, String subject, String content) {
        try {
            Subscriber subscriber = subscriberRepository.findById(subscriberId).orElseThrow();
            emailService.sendSimpleMessage(subscriber.getEmail(), subject, content);

            HashMap<Object, Object> body = new HashMap<>();
            body.put("status", "mail_send_ok");
            return ResponseEntity.status(HttpStatus.OK).body(body);
        } catch (Exception e) {
            HashMap<Object, Object> body = new HashMap<>();
            body.put("status", "mail_send_failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }
}
