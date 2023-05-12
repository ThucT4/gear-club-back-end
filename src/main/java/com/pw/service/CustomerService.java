package com.pw.service;

import com.pw.model.Customer;
import com.pw.model.HttpResponse;
import com.pw.model.Product;
import com.pw.model.Role;
import com.pw.repository.CustomerRepository;
import com.pw.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@Slf4j
public class CustomerService extends CrudService<Customer> {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

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
                continue;
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
        } else if(quantity > product.getQuantity()) {
            return HttpResponse.builder().status("bad").message("database_error").build();
        } else {

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

            ArrayList<HashMap<Integer, Integer>> cartList = (ArrayList<HashMap<Integer, Integer>>) customer.getShoppingCart().clone();
            HashMap<Integer, Integer> currentCart = cartList.get(cartList.size() - 1);
            currentCart.put(productId, currentCart.get(productId) - 1);
            customer.setShoppingCart(cartList);
            customerRepository.save(customer);

            return HttpResponse.builder().status("ok").message("success").build();
        }
    }

    public HttpResponse payment(int customerId) {
        // Get current
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new EntityNotFoundException("Customer with id: "+ customerId + " not found"));
        ArrayList<HashMap<Integer, Integer>> shoppingCart = (ArrayList<HashMap<Integer, Integer>>) customer.getShoppingCart().clone();
        HashMap<Integer, Integer> latestCart = shoppingCart.get(shoppingCart.size() - 1);

        // Decrease quantity of products inside cart
        for(Map.Entry<Integer,Integer> cart : latestCart.entrySet()) {
            if (cart.getKey() < 0) { // Skip -1, -2, -3
                continue;
            }

            Product product = productCrudService.retrieve(cart.getKey());
            if(cart.getValue() > product.getQuantity()) {
                return HttpResponse.builder().status("bad").message("not enough quantity: " + cart.getKey()).build();
            } else {
                product.setQuantity(product.getQuantity() - cart.getValue());
                productCrudService.update(product);
            }
        }

        // Set payment time + status + total price of this cart
        Long unixTime = Instant.now().getEpochSecond();
        latestCart.put(-2, 2); // Status change from 1 -> 2
        latestCart.put(-3, unixTime.intValue()); // Set payment time
        latestCart.put(-4, getTotalPrice(latestCart)); // Set total price

        // Create new cart
        HashMap<Integer, Integer> newCart = new HashMap<>();
        newCart.put(-1, shoppingCart.size()); // Index of the new cart in cart list
        newCart.put(-2, 1); // Set status for new cart

        shoppingCart.add(newCart);
        customer.setShoppingCart(shoppingCart);
        customerRepository.save(customer);

        return HttpResponse.builder().status("ok").message("success").build();
    }

    public Customer updateCustomerInformation(Integer customerId, Customer customerFromClient) {
        Customer customerDB = customerRepository.findById(customerId).orElseThrow(() -> {throw new EntityNotFoundException("User not found");});

        customerDB.setFirstName(customerFromClient.getFirstName());
        customerDB.setLastName(customerFromClient.getLastName());
        customerDB.setPhone(customerFromClient.getPhone());
        customerDB.setShippingAddress(customerFromClient.getShippingAddress());

        customerRepository.save(customerDB);

        return customerDB;
    }

    public ResponseEntity<List<HashMap<Object, Object>>> getAllPurchasedCarts(Customer customerPrinciple) {
        Customer customer = customerRepository.findById(customerPrinciple.getId()).orElseThrow();
        List<HashMap<Object, Object>> resBody = new ArrayList<>();

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
                        resProduct.put("id", curProduct.getId());
                        resProduct.put("name", curProduct.getName());
                        resProduct.put("price", curProduct.getPrice());
                        resProduct.put("image", curProduct.getImages().get(0));
                        resProduct.put("paymentQuantity", value);

                        productList.add(resProduct);
                    }
                }
            }

            resBody.add(resCart);
        }

        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    public List<Customer> searchByString(Map<String, String> searchRequest) {
        String searchString = searchRequest.get("search");
        List< Customer> customers = customerRepository.findAll();

        // If search string is empty => Return all product
        if (searchString.isEmpty()) {
            return customers;
        }

        // Split string into words first
        String[] words = searchString.toLowerCase().split("\\P{L}+");

        // Filter
        List<Customer> result = new ArrayList<>();
        for (Customer customer : customers) {
            for (String word : words) {
                if (word.isEmpty()) {
                    continue;
                } else  {
                    if (customer.getEmail().toLowerCase().contains(word) ||
                    customer.getFirstName().toLowerCase().contains(word) ||
                    customer.getLastName().toLowerCase().contains(word)) {
                        result.add(customer);
                    }
                }
            }
        }

        return result;
    }

    public ResponseEntity<HashMap<Object, Object>> getCurrentCart(Customer customerPrinciple) {
        Customer customer = customerRepository.findById(customerPrinciple.getId()).orElseThrow();
        HashMap<Object, Object> resCart = new HashMap<>();

        HashMap<Integer, Integer> curCart = customer.getShoppingCart().get(customer.getShoppingCart().size() - 1);
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
            } else {
                Optional<Product> curOptionalProduct = productRepository.findById(key);

                if (curOptionalProduct.isPresent()) {
                    Product curProduct = curOptionalProduct.get();

                    HashMap<String, Object> resProduct = new HashMap<String, Object>();
                    resProduct.put("id", curProduct.getId());
                    resProduct.put("name", curProduct.getName());
                    resProduct.put("price", curProduct.getPrice());
                    resProduct.put("images", curProduct.getImages());
                    resProduct.put("paymentQuantity", value);
                    resProduct.put("quantity", curProduct.getQuantity()); // Total quantity

                    productList.add(resProduct);
                }
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(resCart);
    }
}
