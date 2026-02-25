package com.revshop.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {

    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private Integer quantity;
    private String categoryName;
}