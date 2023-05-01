package com.pw.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "CartItem")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CartItem extends AbstractEntity {
    @OneToOne
    @JoinColumn(name = "PRODUCT_ID", referencedColumnName = "id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "SHOPPING_CART_ID", referencedColumnName = "id")
    private ShoppingCart shoppingCart;

    @Column(name = "QUANTITY")
    private Integer quantity;
}
