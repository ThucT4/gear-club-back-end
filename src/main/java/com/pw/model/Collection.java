package com.pw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashMap;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Collections")
@EntityListeners(AuditingEntityListener.class)
public class Collection extends AbstractEntity {
    // Name of collection
    @NotBlank
    @Size(max = 150)
    @Column(name = "NAME")
    private String name;

    // Array of product's ID
//    @ElementCollection
//    @NotNull
//    @Column(name = "PRODUCTLIST")
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "PRODUCTLIST")
    private HashMap<Integer, String> productList;
}
