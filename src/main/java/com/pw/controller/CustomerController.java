package com.pw.controller;

import com.pw.model.Customer;
import com.pw.model.HttpResponse;
import com.pw.service.CustomerService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/customer")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class CustomerController {

    @Value("${stripe.public.key}")
    private String stripePublicKey;

    @Autowired
    private CustomerService customerService;

    @GetMapping(value = "/personal-information")
    public Customer getInformation(@AuthenticationPrincipal Customer customer) {
        return customerService.retrieve(customer.getId());
    }

    @PutMapping(value = "/update-personal-information")
    public Customer updateCustomerInformation(@AuthenticationPrincipal Customer authenticatedCustomer, @RequestBody Customer customerFromClient) {
        return customerService.updateCustomerInformation(authenticatedCustomer.getId(), customerFromClient);
    }

    @GetMapping(value = "/purchased-carts/all", produces = "application/json")
    public ResponseEntity<List<HashMap<Object, Object>>> getAllPurchasedCarts(@AuthenticationPrincipal Customer customer) {
        return customerService.getAllPurchasedCarts(customer);
    }

    @GetMapping(value = "/cart/current", produces = "application/json")
    public ResponseEntity<HashMap<Object, Object>> getCurrentCart(@AuthenticationPrincipal Customer customer) {
        return customerService.getCurrentCart(customer);
    }

    @PutMapping(value = "/cart/add-item", consumes = "application/json")
    public HttpResponse addItem(@AuthenticationPrincipal Customer customer, @RequestBody Map<String, Integer> productForm) {
        return customerService.serviceAddToCart(customer.getId(), productForm.get("productId"), productForm.get("quantity"));
    }

    @PutMapping(value = "/cart/remove-item", consumes = "application/json")
    public HttpResponse removeItem(@AuthenticationPrincipal Customer customer, @RequestBody Map<String, Integer> productForm) {
        return customerService.serviceRemoveFromCart(customer.getId(), productForm.get("productId"));
    }

    @PutMapping(value = "/cart/increase-qty", consumes = "application/json")
    public HttpResponse increaseQuantity(@AuthenticationPrincipal Customer customer, @RequestBody Map<String, Integer> productForm) {
        return customerService.increaseQuantityCart(customer.getId(), productForm.get("productId"));
    }

    @PutMapping(value = "/cart/reduce-qty", consumes = "application/json")
    public HttpResponse decreaseQuantity(@AuthenticationPrincipal Customer customer, @RequestBody Map<String, Integer> productForm) {
        return customerService.decreaseQuantityCart(customer.getId(), productForm.get("productId"));
    }

    @PutMapping(value = "/cart/payment")
    public HttpResponse payment(@AuthenticationPrincipal Customer customer) {
        return customerService.payment(customer.getId());
    }

    @GetMapping(value = "/cart/find-all", consumes = "application/json")
    public List<HashMap<Integer, Integer>> retrieveAllCart(@AuthenticationPrincipal Customer customer) {
        return customerService.findAllCart(customer.getId());
    }

    @PostMapping(value = "/cart/create-payment")
    public String createPayment(@AuthenticationPrincipal Customer customer) throws StripeException {
        PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
                .setCurrency("VND")
                .setAmount((long) customerService.getTotalPrice(customerService.retrieveLatestCart(customer.getId())))
                .build();
        PaymentIntent paymentIntent = PaymentIntent.create(createParams);
        return paymentIntent.getClientSecret();
    }



    // TESTING API: DO NOT USE THOSE APIS
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

    @DeleteMapping(value = "/{id}")
    public String delete(@PathVariable int id) {
        customerService.delete(id);
        return "Done";
    }
}
