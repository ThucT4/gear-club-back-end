package com.pw.controller;

import com.pw.model.Customer;
import com.pw.model.HttpResponse;
import com.pw.service.CustomerService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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

    @Value("${stripe.api.key}")
    private String stripeTestKey;

    @Autowired
    private CustomerService customerService;

    @GetMapping(value = "/all")
    public List<Customer> getAll() {
        return customerService.findAll();
    }

    @PutMapping(value = "/search-by-string")
    public List<Customer> searchByString(@RequestBody Map<String, String> searchRequest) {
        return customerService.searchByString(searchRequest);
    }

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

    /**
     * This path will be called after the customer has successfully checked out with stripe
     * Then this path will make the real changes inside the database (decrease product quantity,...)
     * Payment flow:
     * - Call /cart/create-payment to create new payment intent
     * - User checkout with Stripe => If success call this path, otherwise redirect home
     * @param customer
     * @return
     */
    @PutMapping(value = "/cart/payment")
    public HttpResponse payment(@AuthenticationPrincipal Customer customer) {
        return customerService.payment(customer.getId());
    }

    @GetMapping(value = "/cart/find-all", consumes = "application/json")
    public List<HashMap<Integer, Integer>> retrieveAllCart(@AuthenticationPrincipal Customer customer) {
        return customerService.findAllCart(customer.getId());
    }

    @PostMapping(value = "/cart/create-payment")
    public ResponseEntity<HashMap<String, String>> createPayment(@AuthenticationPrincipal Customer customer) throws StripeException {
        HashMap<String, String> json = new HashMap<>();

        Stripe.apiKey = stripeTestKey;
        PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
                .setCurrency("VND")
                .setAmount((long) customerService.getTotalPrice(customerService.retrieveLatestCart(customer.getId())))
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods
                                .builder()
                                .setEnabled(true)
                                .build()
                )
                .build();
        PaymentIntent paymentIntent = PaymentIntent.create(createParams);

        json.put("clientSecret", paymentIntent.getClientSecret());

        return ResponseEntity.status(HttpStatus.OK).body(json);
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
