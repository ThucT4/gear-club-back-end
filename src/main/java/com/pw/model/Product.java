package com.pw.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
 
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import java.util.ArrayList;
import java.util.HashMap;

@Entity
@Table(name = "Products")
@Getter @Setter @NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Product extends AbstractEntity {
    // Name of product
    @NotBlank
    @Size(max = 50)
    @Column(name = "NAME")
    private String name;

    // Array list of images
    @Size(min = 1, max = 10)
    @Column(name = "IMAGES", length = 1024)
    private ArrayList<String> images;
 
    // Name of vendor
    @NotBlank
    @Size(min = 2, max = 50)
    @Column(name = "VENDORNAME")
    private String vendorName;

    // Image of vendor
    @Column(name = "VENDORIMAGE", length = 400)
    private String vendorImage;

    // Price
    @NotBlank
    @Size(min = 4, max = 50)
    @Column(name = "PRICE")
    private String price;

    // design location
    @Size(max = 50)
    @Column(name = "DESIGNLOCATION")
    private String designLocation;

    // Warranty
    @Size(max = 50)
    @Column(name = "WARRANTY")
    private String warranty;

    // Variants
    @Column(name = "VARIANTS")
    private ArrayList<String> variants;

    // Introduction
    @NotBlank
    @Column(name = "INTRODUCTION", length = 1024, columnDefinition = "text")
    private String intro;

    // Array list of features
    @Size(min = 2, max = 20)
    @Column(name = "FEATURES")
    private HashMap<String, ArrayList<String>> features;
}
