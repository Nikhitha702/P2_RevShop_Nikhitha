package com.revshop.service;

import com.revshop.dto.ApiResponse;
import com.revshop.entity.CartItem;
import com.revshop.entity.Product;
import com.revshop.entity.User;
import com.revshop.repository.CartItemRepository;
import com.revshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public ApiResponse addToCart(Long productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        User buyer = currentUserService.getCurrentUserOrThrow();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (!product.isActive()) {
            throw new IllegalArgumentException("Product is not available");
        }

        CartItem item = cartItemRepository.findByBuyerAndProductId(buyer, productId)
                .orElseGet(CartItem::new);

        int updatedQuantity = (item.getQuantity() == null ? 0 : item.getQuantity()) + quantity;
        if (product.getQuantity() < updatedQuantity) {
            throw new IllegalArgumentException("Requested quantity not in stock");
        }

        item.setBuyer(buyer);
        item.setProduct(product);
        item.setQuantity(updatedQuantity);
        cartItemRepository.save(item);

        return new ApiResponse(true, "Product added to cart");
    }

    public List<CartItem> getMyCart() {
        return cartItemRepository.findByBuyer(currentUserService.getCurrentUserOrThrow());
    }

    @Transactional
    public ApiResponse updateCartItem(Long cartItemId, Integer quantity) {
        User buyer = currentUserService.getCurrentUserOrThrow();
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        if (!item.getBuyer().getId().equals(buyer.getId())) {
            throw new IllegalArgumentException("You can only modify your cart items");
        }

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Product product = item.getProduct();
        if (!product.isActive()) {
            throw new IllegalArgumentException("Product is not available");
        }

        if (quantity > product.getQuantity()) {
            throw new IllegalArgumentException("Requested quantity not in stock");
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return new ApiResponse(true, "Cart item updated");
    }

    @Transactional
    public ApiResponse removeCartItem(Long cartItemId) {
        User buyer = currentUserService.getCurrentUserOrThrow();
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        if (!item.getBuyer().getId().equals(buyer.getId())) {
            throw new IllegalArgumentException("You can only remove your cart items");
        }

        cartItemRepository.delete(item);
        return new ApiResponse(true, "Item removed from cart");
    }

    public BigDecimal totalAmount() {
        return getMyCart().stream()
                .map(item -> {
                    BigDecimal unit = item.getProduct().getDiscountedPrice() != null
                            ? item.getProduct().getDiscountedPrice()
                            : item.getProduct().getMrp();
                    return unit.multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
