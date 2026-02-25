package com.revshop.service;

import com.revshop.dto.ProductRequest;
import com.revshop.entity.*;
import com.revshop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;

    // 🔹 ADD PRODUCT (SELLER ONLY)
    public String addProduct(ProductRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Seller seller = sellerRepository.findAll()
                .stream()
                .filter(s -> s.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Seller profile not found"));

        Category category = categoryRepository
                .findByName(request.getCategoryName())
                .orElseGet(() ->
                        categoryRepository.save(
                                Category.builder()
                                        .name(request.getCategoryName())
                                        .build()
                        )
                );

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .discountedPrice(request.getDiscountedPrice())
                .quantity(request.getQuantity())
                .seller(seller)
                .category(category)
                .createdAt(LocalDateTime.now())
                .build();

        productRepository.save(product);

        return "Product Added Successfully";
    }

    // 🔹 GET ALL PRODUCTS
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    // 🔹 GET PRODUCT BY ID
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    // 🔹 SEARCH PRODUCTS
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }
}