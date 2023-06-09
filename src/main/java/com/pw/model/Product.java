package com.pw.model;

import com.pw.converter.HashMapConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.HashMap;

@Entity
@Table(name = "Products")
@AllArgsConstructor
@RequiredArgsConstructor
@Data
@EntityListeners(AuditingEntityListener.class)
public class Product extends AbstractEntity {
    // Name of product
    @NotBlank
    @Column(name = "NAME")
    @Size(min = 1, max = 150)
    private String name;

    // Array list of images
    @Column(name = "IMAGES", length = 1024)
    @Size(min = 1, max = 20)
    private ArrayList<String> images;
 
    // Name of vendor
    @NotBlank
    @Column(name = "VENDORNAME")
    @Size(min = 2, max = 50)
    private String vendorName;

    // Price
    @NonNull
    @Column(name = "PRICE")
    private Long price;

    // design location
    @Size(max = 50)
    @Column(name = "DESIGNLOCATION")
    private String designLocation;

    // Warranty
    // @Size(max = 100)
    @Column(name = "WARRANTY")
    private Integer warranty;

    // Introduction
    @NotBlank
    @Column(name = "INTRODUCTION", length = 1024, columnDefinition = "text")
    private String intro;

    // Title of description
    @Column(name = "TITLEDESCRIPTION", length = 1024, columnDefinition = "text")
    private String title;

    // Description
    @NotBlank
    @Column(name = "Description", length = 1024, columnDefinition = "text")
    private String description;

    // Array list of features
    @Convert(converter =  HashMapConverter.class)
    @Column(name = "FEATURES", length = 1024 * 10)
    private HashMap<String, String> features;

    // Category
    @NotBlank
    @Column(name = "CATEGORY")
    private String category;

    // Highlights
    @Convert(converter =  HashMapConverter.class)
    @Column(name = "HIGHLIGHTS", length = 1024 * 10)
    private HashMap<String, String> highlights;

    // Quantity
    @Column(name = "QUANTITY")
    private Integer quantity;

    @Column(name = "DELETED")
    @Value("${props.boolean.deleted:#{false}}")
    private Boolean deleted = false;

    public Product() {
    }
}
