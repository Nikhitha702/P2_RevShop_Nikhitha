package com.revshop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SellerPaymentOverviewResponse {
    private BigDecimal totalPaidAmount;
    private long paidOrdersCount;
    private long unpaidOrdersCount;
    private List<SellerPaymentOrderView> orders;
}
