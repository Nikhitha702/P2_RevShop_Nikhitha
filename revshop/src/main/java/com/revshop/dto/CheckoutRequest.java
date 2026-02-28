package com.revshop.dto;

import lombok.Data;

@Data
public class CheckoutRequest {
    private String shippingAddress;
    private String billingAddress;
}
