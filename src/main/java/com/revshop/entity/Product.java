package com.revshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal mrp;

    @Column(precision = 12, scale = 2)
    private BigDecimal discountedPrice;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer inventoryThreshold = 5;

    @Column(length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties({"active"})
    private Category category;

    @ManyToOne(optional = false)
    @JoinColumn(name = "seller_id")
    @JsonIgnoreProperties({"password", "address", "phone", "businessName", "gstNumber", "businessCategory", "enabled"})
    private User seller;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
