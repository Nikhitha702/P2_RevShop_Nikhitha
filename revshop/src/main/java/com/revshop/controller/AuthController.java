package com.revshop.controller;

import com.revshop.dto.BuyerRegisterRequest;
import com.revshop.dto.SellerRegisterRequest;
import com.revshop.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    // 🛍 Buyer Registration
    @PostMapping("/register/buyer")
    public String registerBuyer(@RequestBody BuyerRegisterRequest request) {
        return authService.registerBuyer(request);
    }

    // 🏬 Seller Registration
    @PostMapping("/register/seller")
    public String registerSeller(@RequestBody SellerRegisterRequest request) {
        return authService.registerSeller(request);
    }

    // 🔐 Login
    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(email, password)
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "Login Successful";
    }

    // 🚪 Logout
    @PostMapping("/logout")
    public String logout() {
        SecurityContextHolder.clearContext();
        return "Logged out successfully";
    }
}