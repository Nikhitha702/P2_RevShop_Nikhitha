package com.revshop.mapper;

import com.revshop.dto.ProductRequest;
import com.revshop.dto.ProductUpdateRequest;
import com.revshop.entity.Category;
import com.revshop.entity.Product;
import com.revshop.entity.User;

public final class ProductMapper {

    private ProductMapper() {
    }

    public static Product toNewEntity(ProductRequest request, Category category, User seller) {
        Product product = new Product();
        product.setName(request.getName().trim());
        product.setDescription(request.getDescription().trim());
        product.setMrp(request.getMrp());
        product.setDiscountedPrice(request.getDiscountedPrice());
        product.setQuantity(request.getQuantity());
        product.setInventoryThreshold(request.getInventoryThreshold() == null ? 5 : request.getInventoryThreshold());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);
        product.setSeller(seller);
        product.setActive(true);
        return product;
    }

    public static void applyUpdate(Product product, ProductUpdateRequest request) {
        if (request.getName() != null && !request.getName().isBlank()) {
            product.setName(request.getName().trim());
        }
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            product.setDescription(request.getDescription().trim());
        }
        if (request.getMrp() != null) {
            product.setMrp(request.getMrp());
        }
        if (request.getDiscountedPrice() != null) {
            product.setDiscountedPrice(request.getDiscountedPrice());
        }
        if (request.getQuantity() != null) {
            product.setQuantity(request.getQuantity());
        }
        if (request.getInventoryThreshold() != null) {
            product.setInventoryThreshold(request.getInventoryThreshold());
        }
        if (request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl());
        }
    }
}
