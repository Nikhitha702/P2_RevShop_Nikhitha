package com.revshop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerRegisterRequest extends BuyerRegisterRequest {

    @NotBlank(message = "Business name is required")
    private String businessName;

    @NotBlank(message = "GST number is required")
    private String gstNumber;

    @NotBlank(message = "Business category is required")
    private String businessCategory;
}
