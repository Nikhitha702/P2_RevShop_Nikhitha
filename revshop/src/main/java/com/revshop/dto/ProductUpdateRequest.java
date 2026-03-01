package com.revshop.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateRequest {
    private String name;
    private String description;
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    private BigDecimal price;
    @DecimalMin(value = "0.00", message = "Discounted price cannot be negative")
    private BigDecimal discountedPrice;
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
    @Min(value = 0, message = "Threshold cannot be negative")
    private Integer inventoryThreshold;
    private String categoryName;
    private Boolean active;
}
