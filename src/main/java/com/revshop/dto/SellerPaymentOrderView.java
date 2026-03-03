package com.revshop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class SellerPaymentOrderView {
    private Long orderId;
    private String buyerName;
    private String orderStatus;
    private String paymentStatus;
    private BigDecimal sellerAmount;
    private String paymentMethod;
    private LocalDateTime paidAt;
}
