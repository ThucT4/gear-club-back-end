package com.pw.service;
 

import com.pw.GearClubApplication;
import com.pw.model.Product;


import com.pw.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


 
@Service
public class ProductService extends CrudService<Product> {

    private static final Logger log = LoggerFactory.getLogger(GearClubApplication.class);
 
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

        productDb.setDesignLocation(product.getDesignLocation());
        productDb.setFeatures(product.getFeatures());

        productDb.setIntro(product.getIntro());
        productDb.setTitle(product.getTitle());
        productDb.setDescription(product.getDescription());

        productDb.setPrice(product.getPrice());

        productDb.setWarranty(product.getWarranty());
        productDb.setCategory(product.getCategory());
        productDb.setQuantity(product.getQuantity());
        productDb.setHighlights(product.getHighlights());
 
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

    public JSONObject getCustom(String link) {
        String[] queries = link.split("&");

        JSONObject resultWrapper = new JSONObject();

        List<Product> result = new ArrayList<Product>(productRepository.findAll());

        int pageNum = 0, itemsPerPage = 0;

        for (String query : queries) {
            String[] querySplit = query.split("=");

            if (querySplit[0].equals("pageNum")) {
                pageNum = Integer.parseInt(querySplit[1]);
            }

            if (querySplit[0].equals("itemsPerPage")) {
                itemsPerPage = Integer.parseInt(querySplit[1]);
            }

            // Filter quantity
            if (querySplit[0].equals("availability")) {
                if (querySplit[1].equals("true")) {
                    result.removeIf(p -> p.getQuantity() < 1);
                }
                else if (querySplit[1].equals("false")) {
                    result.removeIf(p -> p.getQuantity() > 0);
                }
            }

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

                    if (querySplit[1].equals("decreasingPrice")) {
                        Collections.reverse(result);
                    }
                }
            }
        }

        // Pagination
        if (pageNum*itemsPerPage > 0 && itemsPerPage < result.size()) {
            if (pageNum*itemsPerPage > result.size()) {
                if ((pageNum - 1)*itemsPerPage < result.size()) {
                    int totalSize = result.size();
                    result = result.subList( (pageNum - 1)*itemsPerPage, result.size());
                    resultWrapper.put("products", result);
                    resultWrapper.put("totalPages", Math.ceil((float) totalSize / (float) itemsPerPage));
                    resultWrapper.put("currentPage", pageNum);
                    resultWrapper.put("itemsPerPage", itemsPerPage);
                    return resultWrapper;
                }

                return null;
            }
            else {
                int totalSize = result.size();
                result = result.subList((pageNum - 1)*itemsPerPage, pageNum*itemsPerPage);
                resultWrapper.put("products", result);
                resultWrapper.put("totalPages", Math.ceil((float) totalSize / (float) itemsPerPage));
                resultWrapper.put("currentPage", pageNum);
                resultWrapper.put("itemsPerPage", itemsPerPage);
                return resultWrapper;
            }
        }

        resultWrapper.put("products", result);
        resultWrapper.put("totalPages", Math.ceil((float)result.size() / (float)itemsPerPage));
        resultWrapper.put("currentPage", pageNum);
        resultWrapper.put("itemsPerPage", itemsPerPage);

        return resultWrapper;
    }
}