package com.revshop.controller;

import com.revshop.entity.User;
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

    // ✅ REGISTER
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        return authService.register(user);
    }

    // ✅ LOGIN (Session-Based Authentication)
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

    // ✅ LOGOUT
    @PostMapping("/logout")
    public String logout() {
        SecurityContextHolder.clearContext();
        return "Logged out successfully";
    }
}