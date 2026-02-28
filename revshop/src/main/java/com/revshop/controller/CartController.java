package com.revshop.controller;

import com.revshop.dto.ApiResponse;
import com.revshop.entity.Cart;
import com.revshop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('BUYER')")
public class CartController {

    private final CartService cartService;

    // Add product to cart
    @PostMapping("/add")
    public ApiResponse addToCart(
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        return cartService.addToCart(productId, quantity);
    }

    // View cart
    @GetMapping
    public Cart viewCart() {
        return cartService.viewCart();
    }

    // Remove item
    @DeleteMapping("/remove")
    public ApiResponse removeItem(@RequestParam Long cartItemId) {
        return cartService.removeFromCart(cartItemId);
    }

    // Update quantity
    @PutMapping("/update")
    public ApiResponse updateQuantity(
            @RequestParam Long cartItemId,
            @RequestParam Integer quantity) {
        return cartService.updateQuantity(cartItemId, quantity);
    }

    // Get total
    @GetMapping("/total")
    public BigDecimal getTotal() {
        return cartService.calculateTotal();
    }

    // Clear cart
    @DeleteMapping("/clear")
    public ApiResponse clearCart() {
        return cartService.clearCart();
    }
}
