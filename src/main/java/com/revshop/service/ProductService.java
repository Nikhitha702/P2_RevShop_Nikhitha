package com.revshop.service;

import com.revshop.dto.ApiResponse;
import com.revshop.dto.ProductRequest;
import com.revshop.dto.ProductUpdateRequest;
import com.revshop.entity.Category;
import com.revshop.entity.Product;
import com.revshop.entity.User;
import com.revshop.mapper.ProductMapper;
import com.revshop.repository.CategoryRepository;
import com.revshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;
    private final FileStorageService fileStorageService;

    @Transactional
    public ApiResponse addProduct(ProductRequest request) {
        User seller = currentUserService.getCurrentUserOrThrow();
        Category category = categoryRepository.findByNameIgnoreCase(request.getCategoryName())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Product product = ProductMapper.toNewEntity(request, category, seller);
        productRepository.save(product);
        notifyIfLowStock(product);

        return new ApiResponse(true, "Product added successfully");
    }

    @Transactional
    public ApiResponse addProductWithImage(ProductRequest request, MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            request.setImageUrl(fileStorageService.storeProductImage(imageFile));
        }
        return addProduct(request);
    }

    @Transactional
    public ApiResponse updateProduct(Long id, ProductUpdateRequest request) {
        User seller = currentUserService.getCurrentUserOrThrow();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new IllegalArgumentException("You can update only your products");
        }

        ProductMapper.applyUpdate(product, request);

        productRepository.save(product);
        notifyIfLowStock(product);
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

    public List<Product> browseAllActiveProducts() {
        return productRepository.findByActiveTrueOrderByCreatedAtDesc();
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

    private void notifyIfLowStock(Product product) {
        if (product.getQuantity() <= product.getInventoryThreshold()) {
            String message = "Low stock alert: " + product.getName() + " has only " + product.getQuantity() + " units left.";
            notificationService.createNotification(product.getSeller(), message);
        }
    }
}
