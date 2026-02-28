package com.revshop.controller;

import com.revshop.dto.CheckoutRequest;
import com.revshop.entity.Order;
import com.revshop.entity.OrderStatus;
import com.revshop.service.OrderService;
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
    public String checkout(@RequestBody(required = false) CheckoutRequest request) {
        return orderService.checkout(request);
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('BUYER')")
    public List<Order> getMyOrders() {
        return orderService.getMyOrders();
    }

    @GetMapping("/seller-orders")
    @PreAuthorize("hasRole('SELLER')")
    public List<Order> getSellerOrders() {
        return orderService.getSellerOrders();
    }

    @PutMapping("/update-status")
    @PreAuthorize("hasRole('SELLER')")
    public String updateStatus(@RequestParam Long orderId, @RequestParam OrderStatus status) {
        return orderService.updateOrderStatus(orderId, status);
    }
}
