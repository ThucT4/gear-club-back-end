package com.pw.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Entity
@Table(name = "Customers")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Customer extends AbstractEntity {

    @NotBlank
    @Size(max = 20)
    @Column(name = "USERNAME")
    private String username;

    @NotBlank
    @Size(max = 50)
    @Column(name = "PASSWORD")
    private String password;

    //First name
    @NotBlank
    @Size(max = 20)
    @Column(name = "FIRSTNAME")
    private String firstName;

    //Customer last name
    @NotBlank
    @Size(max = 50)
    @Column(name = "LASTNAME")
    private String lastName;

    //Customer email
    @NotBlank
    @Size(max = 320)
    @Column(name = "EMAIL")
    private String email;

    //Customer phone
    @Size(max = 15)
    @Column(name = "PHONE")
    private String phone;

    //Customer address
    @Size(max = 1024)
    @Column(name = "ADDRESS")
    private String shippingAddress;

    //
    @Column(name = "ShoppingCart")
    private List<HashMap<Integer, Integer>> shoppingCart = new ArrayList<>();
}
