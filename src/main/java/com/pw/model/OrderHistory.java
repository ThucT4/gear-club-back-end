package com.pw.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "OrderHistory")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class OrderHistory extends AbstractEntity {
    // Date of order
    @Column(name = "ORDER_DATE")
    private LocalDate orderDate;

    // Total price of order
    @Column(name = "TOTAL_PRICE")
    private BigDecimal totalPrice;

    // One-to-many relationship with CartItem entity
    @OneToMany(mappedBy = "OrderHistory")
    private List<CartItem> items = new ArrayList<>();

    // Many-to-one relationship with Customer entity
    @ManyToOne
    @JoinColumn(name = "CUSTOMER_ID", referencedColumnName = "id")
    private Customer customer;
}