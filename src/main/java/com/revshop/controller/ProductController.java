package com.revshop.controller;

import com.revshop.dto.ApiResponse;
import com.revshop.dto.ProductRequest;
import com.revshop.dto.ProductUpdateRequest;
import com.revshop.entity.Product;
import com.revshop.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public Page<Product> browseProducts(Pageable pageable) {
        return productService.browseProducts(pageable);
    }

    @GetMapping("/all")
    public List<Product> browseAllProducts() {
        return productService.browseAllActiveProducts();
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @GetMapping("/search")
    public Page<Product> searchProducts(@RequestParam String keyword, Pageable pageable) {
        return productService.searchProducts(keyword, pageable);
    }

    @GetMapping("/category/{categoryName}")
    public Page<Product> browseByCategory(@PathVariable String categoryName, Pageable pageable) {
        return productService.browseByCategory(categoryName, pageable);
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ApiResponse addProduct(@Valid @RequestBody ProductRequest request) {
        return productService.addProduct(request);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('SELLER')")
    public ApiResponse addProductWithImage(@RequestParam String name,
                                           @RequestParam String description,
                                           @RequestParam java.math.BigDecimal mrp,
                                           @RequestParam(required = false) java.math.BigDecimal discountedPrice,
                                           @RequestParam Integer quantity,
                                           @RequestParam(required = false) Integer inventoryThreshold,
                                           @RequestParam String categoryName,
                                           @RequestParam(required = false) String imageUrl,
                                           @RequestParam(required = false) MultipartFile imageFile) {
        ProductRequest request = new ProductRequest();
        request.setName(name);
        request.setDescription(description);
        request.setMrp(mrp);
        request.setDiscountedPrice(discountedPrice);
        request.setQuantity(quantity);
        request.setInventoryThreshold(inventoryThreshold);
        request.setCategoryName(categoryName);
        request.setImageUrl(imageUrl);
        return productService.addProductWithImage(request, imageFile);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ApiResponse updateProduct(@PathVariable Long id, @Valid @RequestBody ProductUpdateRequest request) {
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ApiResponse deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }

    @GetMapping("/inventory")
    @PreAuthorize("hasRole('SELLER')")
    public List<Product> inventory() {
        return productService.getSellerInventory();
    }

    @GetMapping("/inventory/low-stock")
    @PreAuthorize("hasRole('SELLER')")
    public List<Product> lowStock() {
        return productService.getLowStockProducts();
    }
}
