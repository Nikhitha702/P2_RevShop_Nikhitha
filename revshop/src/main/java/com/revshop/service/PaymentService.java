package com.revshop.service;

import com.revshop.dto.ApiResponse;
import com.revshop.entity.*;
import com.revshop.repository.OrderRepository;
import com.revshop.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;

    @Transactional
    public ApiResponse pay(Long orderId, PaymentMethod method) {
        User buyer = currentUserService.getCurrentUserOrThrow();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getBuyer().getId().equals(buyer.getId())) {
            throw new IllegalArgumentException("You can pay only your own order");
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setMethod(method);
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        notificationService.createNotification(buyer, "Payment successful for order " + order.getId());
        return new ApiResponse(true, "Payment successful");
    }
}
