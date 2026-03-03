package com.revshop.controller;

import com.revshop.dto.ApiResponse;
import com.revshop.dto.BuyerRegisterRequest;
import com.revshop.dto.ForgotPasswordRequest;
import com.revshop.dto.ForgotPasswordResponse;
import com.revshop.dto.ResetPasswordRequest;
import com.revshop.dto.SellerRegisterRequest;
import com.revshop.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/buyer")
    public ApiResponse registerBuyer(@Valid @RequestBody BuyerRegisterRequest request) {
        return authService.registerBuyer(request);
    }

    @PostMapping("/register/seller")
    public ApiResponse registerSeller(@Valid @RequestBody SellerRegisterRequest request) {
        return authService.registerSeller(request);
    }

    @PostMapping("/forgot-password")
    public ForgotPasswordResponse forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return authService.forgotPassword(request);
    }

    @PostMapping("/reset-password")
    public ApiResponse resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return authService.resetPassword(request);
    }
}
