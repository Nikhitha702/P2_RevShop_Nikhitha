package com.revshop.dto;

import lombok.Data;

@Data
public class SellerRegisterRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;

    private String businessName;
    private String gstNumber;
    private String address;
    private String phone;
    private String category;
}