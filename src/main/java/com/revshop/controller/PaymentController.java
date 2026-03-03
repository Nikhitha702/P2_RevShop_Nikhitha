package com.revshop.controller;

import com.revshop.dto.ApiResponse;
import com.revshop.dto.SellerPaymentOverviewResponse;
import com.revshop.entity.PaymentMethod;
import com.revshop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/pay")
    @PreAuthorize("hasRole('BUYER')")
    public ApiResponse pay(@RequestParam Long orderId, @RequestParam PaymentMethod method) {
        return paymentService.pay(orderId, method);
    }

    @GetMapping("/seller-overview")
    @PreAuthorize("hasRole('SELLER')")
    public SellerPaymentOverviewResponse sellerOverview() {
        return paymentService.sellerOverview();
    }
}
