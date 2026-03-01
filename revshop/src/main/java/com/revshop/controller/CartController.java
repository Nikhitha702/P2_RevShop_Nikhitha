package com.revshop.controller;

import com.revshop.dto.ApiResponse;
import com.revshop.entity.CartItem;
import com.revshop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('BUYER')")
    public ApiResponse addToCart(@RequestParam Long productId, @RequestParam Integer quantity) {
        return cartService.addToCart(productId, quantity);
    }

    @GetMapping
    @PreAuthorize("hasRole('BUYER')")
    public List<CartItem> myCart() {
        return cartService.getMyCart();
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('BUYER')")
    public ApiResponse updateCartItem(@RequestParam Long cartItemId, @RequestParam Integer quantity) {
        return cartService.updateCartItem(cartItemId, quantity);
    }

    @DeleteMapping("/remove")
    @PreAuthorize("hasRole('BUYER')")
    public ApiResponse removeCartItem(@RequestParam Long cartItemId) {
        return cartService.removeCartItem(cartItemId);
    }

    @GetMapping("/total")
    @PreAuthorize("hasRole('BUYER')")
    public BigDecimal total() {
        return cartService.totalAmount();
    }
}
