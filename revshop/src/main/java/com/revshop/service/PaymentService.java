package com.revshop.service;

import com.revshop.dto.ApiResponse;
import com.revshop.dto.SellerPaymentOrderView;
import com.revshop.dto.SellerPaymentOverviewResponse;
import com.revshop.entity.*;
import com.revshop.mapper.PaymentMapper;
import com.revshop.repository.OrderRepository;
import com.revshop.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        paymentRepository.findByOrder(order).ifPresent(p -> {
            throw new IllegalArgumentException("Payment already completed for this order");
        });

        Payment payment = PaymentMapper.toNewPayment(order, method);
        paymentRepository.save(payment);

        notificationService.createNotification(buyer, "Payment successful for order " + order.getId());
        return new ApiResponse(true, "Payment successful");
    }

    @Transactional(readOnly = true)
    public SellerPaymentOverviewResponse sellerOverview() {
        User seller = currentUserService.getCurrentUserOrThrow();
        List<Order> orders = orderRepository.findDistinctByItemsSellerOrderByCreatedAtDesc(seller);
        if (orders.isEmpty()) {
            return new SellerPaymentOverviewResponse(BigDecimal.ZERO, 0, 0, List.of());
        }

        Map<Long, Payment> paymentsByOrderId = paymentRepository.findByOrderIn(orders).stream()
                .collect(Collectors.toMap(p -> p.getOrder().getId(), Function.identity(), (left, right) -> left));

        BigDecimal totalPaidAmount = BigDecimal.ZERO;
        long paidOrdersCount = 0;
        List<SellerPaymentOrderView> orderViews = new ArrayList<>();

        for (Order order : orders) {
            BigDecimal sellerAmount = order.getItems().stream()
                    .filter(i -> i.getSeller().getId().equals(seller.getId()))
                    .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Payment payment = paymentsByOrderId.get(order.getId());
            boolean paid = payment != null && payment.getStatus() == PaymentStatus.SUCCESS;
            if (paid) {
                totalPaidAmount = totalPaidAmount.add(sellerAmount);
                paidOrdersCount++;
            }
            orderViews.add(PaymentMapper.toSellerOrderView(order, seller, payment, sellerAmount));
        }

        long unpaidOrdersCount = Math.max(orders.size() - paidOrdersCount, 0);
        return new SellerPaymentOverviewResponse(totalPaidAmount, paidOrdersCount, unpaidOrdersCount, orderViews);
    }
}
