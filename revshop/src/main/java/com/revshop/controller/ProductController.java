package com.revshop.controller;

import com.revshop.dto.ProductRequest;
import com.revshop.entity.Product;
import com.revshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 🔹 ADD PRODUCT (SELLER ONLY)
    @PostMapping
    public String addProduct(@RequestBody ProductRequest request) {
        return productService.addProduct(request);
    }

    // 🔹 GET ALL PRODUCTS
    @GetMapping
    public Page<Product> getAllProducts(Pageable pageable) {
        return productService.getAllProducts(pageable);
    }

    // 🔹 GET PRODUCT BY ID
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    // 🔹 SEARCH PRODUCT
    @GetMapping("/search")
    public Page<Product> searchProducts(
            @RequestParam String keyword,
            Pageable pageable) {
        return productService.searchProducts(keyword, pageable);
    }
}