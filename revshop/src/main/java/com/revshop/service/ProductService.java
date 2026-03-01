package com.revshop.service;

import com.revshop.dto.ApiResponse;
import com.revshop.dto.ProductRequest;
import com.revshop.dto.ProductUpdateRequest;
import com.revshop.entity.Category;
import com.revshop.entity.Product;
import com.revshop.entity.User;
import com.revshop.repository.CategoryRepository;
import com.revshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public ApiResponse addProduct(ProductRequest request) {
        User seller = currentUserService.getCurrentUserOrThrow();
        Category category = categoryRepository.findByNameIgnoreCase(request.getCategoryName())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

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
        productRepository.save(product);

        return new ApiResponse(true, "Product added successfully");
    }

    @Transactional
    public ApiResponse updateProduct(Long id, ProductUpdateRequest request) {
        User seller = currentUserService.getCurrentUserOrThrow();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new IllegalArgumentException("You can update only your products");
        }

        if (request.getName() != null && !request.getName().isBlank()) product.setName(request.getName().trim());
        if (request.getDescription() != null && !request.getDescription().isBlank()) product.setDescription(request.getDescription().trim());
        if (request.getMrp() != null) product.setMrp(request.getMrp());
        if (request.getDiscountedPrice() != null) product.setDiscountedPrice(request.getDiscountedPrice());
        if (request.getQuantity() != null) product.setQuantity(request.getQuantity());
        if (request.getInventoryThreshold() != null) product.setInventoryThreshold(request.getInventoryThreshold());
        if (request.getImageUrl() != null) product.setImageUrl(request.getImageUrl());

        productRepository.save(product);
        return new ApiResponse(true, "Product updated successfully");
    }

    @Transactional
    public ApiResponse deleteProduct(Long id) {
        User seller = currentUserService.getCurrentUserOrThrow();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new IllegalArgumentException("You can delete only your products");
        }

        product.setActive(false);
        productRepository.save(product);
        return new ApiResponse(true, "Product removed from catalog");
    }

    public Page<Product> browseProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable);
    }

    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findByActiveTrueAndNameContainingIgnoreCase(keyword, pageable);
    }

    public Page<Product> browseByCategory(String categoryName, Pageable pageable) {
        return productRepository.findByActiveTrueAndCategoryNameIgnoreCase(categoryName, pageable);
    }

    public Product getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (!product.isActive()) {
            throw new IllegalArgumentException("Product is not available");
        }
        return product;
    }

    public List<Product> getSellerInventory() {
        return productRepository.findBySeller(currentUserService.getCurrentUserOrThrow());
    }

    public List<Product> getLowStockProducts() {
        return getSellerInventory().stream()
                .filter(p -> p.getQuantity() <= p.getInventoryThreshold())
                .toList();
    }
}
