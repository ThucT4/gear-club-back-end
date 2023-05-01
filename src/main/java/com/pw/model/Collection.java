package com.pw.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
 
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.ArrayList;
import java.util.HashMap;

@Entity
@Table(name = "Collections")
@Getter @Setter @NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Collection extends AbstractEntity {
    // Name of collection
    @NotBlank
    @Size(max = 150)
    @Column(name = "NAME")
    private String name;

    // Array of product's ID
    @NotNull
    @Column(name = "PRODUCTLIST")
    private HashMap<Integer, String> productList;
    
}
