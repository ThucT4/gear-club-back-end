package com.pw.service;
 
import com.pw.model.Product;
import com.pw.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
 
@Service
public class ProductService extends CrudService<Product> {
 
    @Autowired
    private ProductRepository productRepository;

    // Create new product
    @Override
    public Product create(Product product) {
        return productRepository.save(product);
    }
 
    // Get the product by ID
    @Override
    public Product retrieve(int id) {
        return productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Product with id: " + id + ", not available."));
    }
 
    // Update product by ID
    @Override
    public Product update(Product product) {
        Product productDb = productRepository.findById(product.getId()).orElseThrow(
                () -> new EntityNotFoundException("Product with id: " + product.getId() + ", not available."));
 
        productDb.setName(product.getName());

        productDb.setImages(product.getImages());

        productDb.setVendorName(product.getVendorName());
        // productDb.setVendorImage(product.getVendorImage());

        // productDb.setDesignLocation(product.getDesignLocation());
        productDb.setFeatures(product.getFeatures());

        productDb.setIntro(product.getIntro());

        productDb.setPrice(product.getPrice());

        productDb.setWarranty(product.getWarranty());

        // productDb.setVariants(product.getVariants());
 
        return productRepository.save(productDb);
    }
 
    // Remove product by ID
    @Override
    public void delete(int id) {
        productRepository.deleteById(id);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> getFirst10() {
        return productRepository.findAll().stream().limit(10).collect(Collectors.toList()) ;

    }

    public List<Product> getSecond10() {
        List<Product> all = productRepository.findAll();

        if (all.size() > 10) {
            if (all.size() < 20) {
                return all.subList(10, all.size()-1);
            }
            else {
                return all.subList(10, 19);
            }
        }
        else {
            throw new EntityNotFoundException("There are no more products.");
        }
    }

    public List<Product> getCustom(String link) {
        String[] queries = link.split("&");

        List<Product> result = new ArrayList<Product>(productRepository.findAll());

        int pageNum = 0, itemsPerPage = 0;

        for (String query : queries) {
            String[] querySplit = query.split("=");

            if (querySplit[0] == "pageNum") {
                pageNum = Integer.parseInt(querySplit[1]);
            }

            if (querySplit[0] == "itemsPerPage") {
                itemsPerPage = Integer.parseInt(querySplit[1]);
            }

            // Filter quantity
            // if (querySplit[0] == "availability") {
            //     for (Product p : all) {
            //         if ( ())
            //     }
            // }

            // Filter brand(s)
            if (querySplit[0].equals("brands")) {
                List<String> brands = Arrays.asList(querySplit[1].split(","));

                result.removeIf(p -> brands.stream().filter(brand -> p.getVendorName().toLowerCase().contains(brand)).collect(Collectors.toList()).size() < 1);
            }

            // Filter category(s)
            if (querySplit[0].equals("categories")) {
                List<String> categories = Arrays.asList(querySplit[1].split(","));

                result.removeIf(p -> categories.stream().filter(cate -> p.getCategory().toLowerCase().contains(cate)).collect(Collectors.toList()).size() < 1);
            }

            // Filter price range
            if (querySplit[0].equals("priceFrom")) {
                Long comPrice = Long.parseLong(querySplit[1]);

                result.removeIf(p -> p.getPrice() < comPrice);
            }

            if (querySplit[0].equals("priceTo")) {
                Long comPrice = Long.parseLong(querySplit[1]);

                result.removeIf(p -> p.getPrice() > comPrice);
            }

            // Sort
            if (querySplit[0].equals("sortType")) {
                String[] alpha = {"aToZ", "zToA"};
                String[] price = {"increasingPrice", "decreasingPrice"};

                if (Arrays.asList(alpha).contains(querySplit[1])) {
                    result = result.stream().sorted((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName())).collect(Collectors.toList());

                    if (querySplit[1].equals("zToA")) {
                        Collections.reverse(result);
                    }
                }

                if (Arrays.asList(price).contains(querySplit[1])) {
                    result = result.stream().sorted((p1, p2) -> p1.getPrice().compareTo(p2.getPrice())).collect(Collectors.toList());

                    if (querySplit[1].equals("zdecreasingPriceToA")) {
                        Collections.reverse(result);
                    }
                }
            }
        }

        if (pageNum*itemsPerPage > 0) {
            if (pageNum*itemsPerPage < result.size()) {
                return result.subList(pageNum*itemsPerPage, pageNum*itemsPerPage);
            }
        }

        // System.out.println("Chuột Logitech G Pro X Superlight Wireless Red".compareToIgnoreCase("Chuột ASUS ROG Harpe Ace Aim Lab Edition"));

        return result;
    }

    public List<Product> getByVendorName(ArrayList<String> vendorNames) {
        List<Product> result = new ArrayList<>();

        for (Product p : productRepository.findAll()) {
            for (String vendorName : vendorNames) {
                if (p.getVendorName().toLowerCase().contains(vendorName.toLowerCase())) {
                    result.add(p);
                }
            }
        }

        return result;

        // return productRepository.findAll().stream()
        //     .filter(p -> vendorNames.contains(p.getVendorName().toLowerCase())).collect(Collectors.toList());
    }
}