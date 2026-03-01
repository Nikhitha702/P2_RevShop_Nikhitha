package com.revshop.controller;

import com.revshop.dto.ApiResponse;
import com.revshop.dto.BuyerRegisterRequest;
import com.revshop.dto.SellerRegisterRequest;
import com.revshop.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register/buyer")
    public ApiResponse registerBuyer(@Valid @RequestBody BuyerRegisterRequest request) {
        return authService.registerBuyer(request);
    }

    @PostMapping("/register/seller")
    public ApiResponse registerSeller(@Valid @RequestBody SellerRegisterRequest request) {
        return authService.registerSeller(request);
    }

    @PostMapping("/login")
    public ApiResponse login(@RequestParam String email,
                             @RequestParam String password) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new ApiResponse(true, "Login Successful");
    }

    @PostMapping("/logout")
    public ApiResponse logout() {
        SecurityContextHolder.clearContext();
        return new ApiResponse(true, "Logged out successfully");
    }
}
