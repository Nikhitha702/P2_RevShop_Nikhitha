package com.revshop.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private Integer quantity;
    private Integer inventoryThreshold;
    private String categoryName;
    private Boolean active;
}
