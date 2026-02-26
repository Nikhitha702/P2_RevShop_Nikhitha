package com.revshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Disable CSRF for Postman testing
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // =============================
                        // AUTH APIs (Public)
                        // =============================
                        .requestMatchers("/api/auth/**").permitAll()

                        // =============================
                        // PRODUCT APIs
                        // =============================

                        // Anyone can view products
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                        // Only SELLER can add/update/delete
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("SELLER")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("SELLER")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("SELLER")

                        // =============================
                        // CART APIs (BUYER only)
                        // =============================
                        .requestMatchers("/api/cart/**").hasRole("BUYER")

                        // =============================
                        // ORDER APIs
                        // =============================

                        // Buyer Checkout & View
                        .requestMatchers("/api/orders/checkout").hasRole("BUYER")
                        .requestMatchers("/api/orders/my-orders").hasRole("BUYER")

                        // Seller Update Status
                        .requestMatchers("/api/orders/update-status").hasRole("SELLER")

                        // Any other request must be authenticated
                        .anyRequest().authenticated()
                )

                // Enable Basic Authentication (for Postman)
                .httpBasic(httpBasic -> {});

        return http.build();
    }

    // Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Authentication Manager
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}