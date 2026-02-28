package com.revshop.service;

import com.revshop.entity.Order;
import com.revshop.entity.OrderStatus;
import com.revshop.entity.Payment;
import com.revshop.entity.PaymentMethod;
import com.revshop.entity.PaymentStatus;
import com.revshop.entity.User;
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
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;

    public String makePayment(Long orderId, PaymentMethod method) {
        User user = currentUserService.getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized payment access");
        }

        if (!order.getStatus().equals(OrderStatus.PLACED)) {
            return "Order already paid or invalid status";
        }

        if (paymentRepository.findByOrderId(orderId).isPresent()) {
            return "Payment already exists for this order";
        }

        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotalAmount())
                .method(method)
                .status(PaymentStatus.SUCCESS)
                .paymentDate(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        if (method == PaymentMethod.CASH_ON_DELIVERY) {
            order.setStatus(OrderStatus.SHIPPED);
        } else {
            order.setStatus(OrderStatus.PAID);
        }

        orderRepository.save(order);
        notificationService.createNotification(user, "Payment successful for order: " + order.getId());
        return "Payment Successful";
    }
}
