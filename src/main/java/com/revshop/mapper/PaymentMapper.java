package com.revshop.mapper;

import com.revshop.dto.SellerPaymentOrderView;
import com.revshop.entity.Order;
import com.revshop.entity.Payment;
import com.revshop.entity.PaymentMethod;
import com.revshop.entity.PaymentStatus;
import com.revshop.entity.User;

import java.math.BigDecimal;

public final class PaymentMapper {

    private PaymentMapper() {
    }

    public static Payment toNewPayment(Order order, PaymentMethod method) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setMethod(method);
        payment.setStatus(PaymentStatus.SUCCESS);
        return payment;
    }

    public static SellerPaymentOrderView toSellerOrderView(Order order, User seller, Payment payment, BigDecimal sellerAmount) {
        boolean paid = payment != null && payment.getStatus() == PaymentStatus.SUCCESS;
        String buyerName = order.getBuyer() == null
                ? "Buyer"
                : (String.valueOf(order.getBuyer().getFirstName()) + " " + String.valueOf(order.getBuyer().getLastName())).trim();

        return new SellerPaymentOrderView(
                order.getId(),
                buyerName.isBlank() ? "Buyer" : buyerName,
                order.getStatus().name(),
                paid ? "PAID" : "UNPAID",
                sellerAmount,
                payment != null && payment.getMethod() != null ? payment.getMethod().name() : null,
                payment != null ? payment.getPaidAt() : null
        );
    }
}
