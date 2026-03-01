package com.revshop.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "MRP is required")
    @DecimalMin(value = "0.01", message = "MRP should be greater than 0")
    private BigDecimal mrp;

    @DecimalMin(value = "0.00", message = "Discount cannot be negative")
    private BigDecimal discountedPrice;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    @Min(value = 0, message = "Inventory threshold cannot be negative")
    private Integer inventoryThreshold;

    @NotBlank(message = "Category name is required")
    private String categoryName;

    private String imageUrl;
}
