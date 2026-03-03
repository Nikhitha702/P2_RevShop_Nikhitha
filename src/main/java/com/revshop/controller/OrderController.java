package com.revshop.controller;

import com.revshop.dto.ApiResponse;
import com.revshop.dto.CheckoutRequest;
import com.revshop.entity.Order;
import com.revshop.entity.OrderStatus;
import com.revshop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    @PreAuthorize("hasRole('BUYER')")
    public ApiResponse checkout(@Valid @RequestBody CheckoutRequest request) {
        return orderService.checkout(request);
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('BUYER')")
    public List<Order> myOrders() {
        return orderService.myOrders();
    }

    @GetMapping("/seller-orders")
    @PreAuthorize("hasRole('SELLER')")
    public List<Order> sellerOrders() {
        return orderService.sellerOrders();
    }

    @PutMapping("/status")
    @PreAuthorize("hasRole('SELLER')")
    public ApiResponse updateStatus(@RequestParam Long orderId, @RequestParam OrderStatus status) {
        return orderService.updateStatus(orderId, status);
    }
}
