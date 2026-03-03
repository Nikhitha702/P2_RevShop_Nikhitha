package com.revshop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ForgotPasswordResponse {
    private boolean success;
    private String message;
    private String token;
}
