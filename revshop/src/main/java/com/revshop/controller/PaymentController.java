package com.revshop.controller;

import com.revshop.entity.PaymentMethod;
import com.revshop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('BUYER')")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/pay")
    public String pay(
            @RequestParam Long orderId,
            @RequestParam PaymentMethod method
    ) {
        return paymentService.makePayment(orderId, method);
    }
}
