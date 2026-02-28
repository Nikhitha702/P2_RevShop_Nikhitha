package com.revshop.dto;

import lombok.Data;

@Data
public class BuyerRegisterRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String address;
}
