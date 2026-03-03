package com.revshop.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductUpdateRequest {
    private String name;
    private String description;

    @DecimalMin(value = "0.01", message = "MRP should be greater than 0")
    private BigDecimal mrp;

    @DecimalMin(value = "0.00", message = "Discount cannot be negative")
    private BigDecimal discountedPrice;

    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    @Min(value = 0, message = "Inventory threshold cannot be negative")
    private Integer inventoryThreshold;

    private String imageUrl;
}
