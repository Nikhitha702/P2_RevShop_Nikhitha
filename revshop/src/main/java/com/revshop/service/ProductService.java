package com.revshop.service;

import com.revshop.dto.ProductRequest;
import com.revshop.dto.ProductUpdateRequest;
import com.revshop.entity.Category;
import com.revshop.entity.Product;
import com.revshop.entity.Seller;
import com.revshop.entity.User;
import com.revshop.repository.CategoryRepository;
import com.revshop.repository.ProductRepository;
import com.revshop.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SellerRepository sellerRepository;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;

    public String addProduct(ProductRequest request) {
        User user = currentUserService.getCurrentUser();
        Seller seller = sellerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Seller profile not found"));

        validateProductPricing(request.getPrice(), request.getDiscountedPrice());
        validateQuantity(request.getQuantity());

        Category category = getOrCreateCategory(request.getCategoryName());

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .discountedPrice(request.getDiscountedPrice() == null ? request.getPrice() : request.getDiscountedPrice())
                .quantity(request.getQuantity())
                .inventoryThreshold(request.getInventoryThreshold() == null ? 5 : request.getInventoryThreshold())
                .active(true)
                .seller(seller)
                .category(category)
                .createdAt(LocalDateTime.now())
                .build();

        productRepository.save(product);
        return "Product Added Successfully";
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }

    public Page<Product> browseByCategory(String categoryName, Pageable pageable) {
        return productRepository.findByCategoryNameIgnoreCase(categoryName, pageable);
    }

    public String updateProduct(Long productId, ProductUpdateRequest request) {
        User user = currentUserService.getCurrentUser();
        Seller seller = sellerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Seller profile not found"));
        Product product = getSellerProduct(seller, productId);

        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getDiscountedPrice() != null) product.setDiscountedPrice(request.getDiscountedPrice());
        if (request.getQuantity() != null) {
            validateQuantity(request.getQuantity());
            product.setQuantity(request.getQuantity());
        }
        if (request.getInventoryThreshold() != null) product.setInventoryThreshold(request.getInventoryThreshold());
        if (request.getActive() != null) product.setActive(request.getActive());
        if (request.getCategoryName() != null && !request.getCategoryName().isBlank()) {
            product.setCategory(getOrCreateCategory(request.getCategoryName()));
        }

        validateProductPricing(product.getPrice(), product.getDiscountedPrice());
        productRepository.save(product);
        notifySellerOnLowStock(product);
        return "Product updated successfully";
    }

    public String deleteProduct(Long productId) {
        User user = currentUserService.getCurrentUser();
        Seller seller = sellerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Seller profile not found"));
        Product product = getSellerProduct(seller, productId);
        productRepository.delete(product);
        return "Product deleted successfully";
    }

    public List<Product> getSellerInventory() {
        User user = currentUserService.getCurrentUser();
        Seller seller = sellerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Seller profile not found"));
        return productRepository.findBySeller(seller);
    }

    public List<Product> getLowStockProducts() {
        User user = currentUserService.getCurrentUser();
        Seller seller = sellerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Seller profile not found"));
        return productRepository.findBySeller(seller).stream()
                .filter(product -> product.getInventoryThreshold() != null && product.getQuantity() <= product.getInventoryThreshold())
                .toList();
    }

    public void notifySellerOnLowStock(Product product) {
        if (product.getInventoryThreshold() != null && product.getQuantity() <= product.getInventoryThreshold()) {
            notificationService.createNotification(product.getSeller().getUser(), "Low stock alert for product: " + product.getName());
        }
    }

    private Product getSellerProduct(Seller seller, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new RuntimeException("Unauthorized product access");
        }
        return product;
    }

    private Category getOrCreateCategory(String categoryName) {
        return categoryRepository.findByName(categoryName)
                .orElseGet(() -> categoryRepository.save(Category.builder().name(categoryName).build()));
    }

    private void validateProductPricing(BigDecimal price, BigDecimal discountedPrice) {
        if (price == null || price.signum() <= 0) {
            throw new RuntimeException("Price must be greater than zero");
        }
        if (discountedPrice != null && discountedPrice.compareTo(price) > 0) {
            throw new RuntimeException("Discounted price cannot be greater than price");
        }
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity < 0) {
            throw new RuntimeException("Quantity cannot be negative");
        }
    }
}
