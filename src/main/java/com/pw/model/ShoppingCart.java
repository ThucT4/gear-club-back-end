package com.pw.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Entity
@Table(name = "ShoppingCart")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ShoppingCart extends AbstractEntity{
    @OneToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;

    @OneToMany(targetEntity = CartItem.class)
    @JoinColumn(name = "cart_item_id", referencedColumnName = "id")
    private List<CartItem> cartItems = new ArrayList<>();

    private int totalPrice;
}
