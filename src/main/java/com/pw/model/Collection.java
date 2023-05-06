package com.pw.model;

import com.pw.converter.HashMapConverterIntegerString;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
    @Convert(converter = HashMapConverterIntegerString.class)
    @Column(name = "PRODUCTLIST", length = 1024 * 10)
    private HashMap<Integer, String> productList;
}
