package com.pw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Entity
@Table(name = "Customers")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Customer extends AbstractEntity implements UserDetails {

    //Customer email
    @NotBlank
    @Size(max = 320)
    @Column(
            name = "EMAIL",
            unique = true,
            nullable = false
    )
    private String email;

    @NotBlank
    @Column(name = "PASSWORD")
    private String password;

    @NotBlank
    @Size(max = 20)
    @Column(name = "FIRSTNAME")
    private String firstName;

    @NotBlank
    @Size(max = 20)
    @Column(name = "LASTNAME")
    private String lastName;

    //Customer phone
    @Size(max = 15)
    @Column(name = "PHONE")
    private String phone;

    //Customer address
    @Size(max = 1024)
    @Column(name = "ADDRESS")
    private String shippingAddress;

    // Customer role
    @Enumerated(EnumType.STRING)
    private Role role;

    // Shopping cart
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ShoppingCart")
    private ArrayList<HashMap<Integer, Integer>> shoppingCart = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
