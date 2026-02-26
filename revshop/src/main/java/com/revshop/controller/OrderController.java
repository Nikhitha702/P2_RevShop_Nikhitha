package com.revshop.controller;

import com.revshop.entity.Order;
import com.revshop.entity.OrderStatus;
import com.revshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // ==========================
    // BUYER CHECKOUT
    // ==========================
    @PostMapping("/checkout")
    public String checkout() {
        return orderService.checkout();
    }

    // ==========================
    // BUYER VIEW ORDERS
    // ==========================
    @GetMapping("/my-orders")
    public List<Order> getMyOrders() {
        return orderService.getMyOrders();
    }

    // ==========================
    // SELLER UPDATE STATUS
    // ==========================
    @PutMapping("/update-status")
    public String updateStatus(@RequestParam Long orderId,
                               @RequestParam String status) {

        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());

        return orderService.updateOrderStatus(orderId, orderStatus);
    }
}