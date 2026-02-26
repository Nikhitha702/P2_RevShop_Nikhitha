package com.revshop.service;

import com.revshop.entity.*;
import com.revshop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // ================= ADD TO CART =================
    public String addToCart(Long productId, Integer quantity) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(
                        Cart.builder().user(user).build()
                ));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> existingItem = cart.getItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {

            CartItem item = existingItem.get();
            Integer newQuantity = item.getQuantity() + quantity;

            item.setQuantity(newQuantity);

            BigDecimal newSubtotal = product.getDiscountedPrice()
                    .multiply(BigDecimal.valueOf(newQuantity));

            item.setSubtotal(newSubtotal);

            cartItemRepository.save(item);

        } else {

            BigDecimal subtotal = product.getDiscountedPrice()
                    .multiply(BigDecimal.valueOf(quantity));

            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .subtotal(subtotal)
                    .build();

            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        return "Product added to cart";
    }

    // ================= VIEW CART =================
    public Cart viewCart() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart is empty"));
    }

    // ================= REMOVE ITEM =================
    public String removeFromCart(Long cartItemId) {

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        cartItemRepository.delete(item);

        return "Item removed from cart";
    }

    // ================= UPDATE QUANTITY =================
    public String updateQuantity(Long cartItemId, Integer quantity) {

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (quantity <= 0) {
            cartItemRepository.delete(item);
            return "Item removed from cart";
        }

        item.setQuantity(quantity);

        BigDecimal newSubtotal = item.getProduct()
                .getDiscountedPrice()
                .multiply(BigDecimal.valueOf(quantity));

        item.setSubtotal(newSubtotal);

        cartItemRepository.save(item);

        return "Cart updated successfully";
    }

    // ================= CALCULATE TOTAL =================
    public BigDecimal calculateTotal() {

        Cart cart = viewCart();

        return cart.getItems()
                .stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ================= CLEAR CART =================
    public String clearCart() {

        Cart cart = viewCart();

        cartItemRepository.deleteAll(cart.getItems());

        return "Cart cleared successfully";
    }
}