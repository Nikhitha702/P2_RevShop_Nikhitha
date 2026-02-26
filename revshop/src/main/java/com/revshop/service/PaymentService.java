package com.revshop.service;

import com.revshop.entity.*;
import com.revshop.repository.OrderRepository;
import com.revshop.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public String makePayment(Long orderId, PaymentMethod method) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getStatus().equals(OrderStatus.PLACED)) {
            return "Order already paid or invalid status";
        }

        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotalAmount())   // BigDecimal to BigDecimal ✔
                .method(method)
                .status(PaymentStatus.SUCCESS)
                .paymentDate(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        return "Payment Successful";
    }
}