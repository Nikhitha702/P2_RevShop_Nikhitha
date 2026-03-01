package com.revshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "buyer_id")
    @JsonIgnoreProperties({"password", "address", "phone", "businessName", "gstNumber", "businessCategory", "enabled"})
    private User buyer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties({"seller"})
    private Product product;

    @Column(nullable = false)
    private Integer quantity;
}
