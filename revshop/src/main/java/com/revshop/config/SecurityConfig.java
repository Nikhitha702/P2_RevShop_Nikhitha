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
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // 🔓 Public Auth APIs
                        .requestMatchers("/api/auth/**").permitAll()

                        // 🔓 Public Product Viewing
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                        // 🔐 Only SELLER can add/update/delete products
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("SELLER")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("SELLER")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("SELLER")

                        // 🔐 Only BUYER can access Cart
                        .requestMatchers("/api/cart/**").hasRole("BUYER")

                        // 🔐 Test endpoints (optional)
                        .requestMatchers("/api/test/buyer").hasRole("BUYER")
                        .requestMatchers("/api/test/seller").hasRole("SELLER")

                        .anyRequest().authenticated()
                )

                // Enable Basic Authentication for Postman
                .httpBasic(httpBasic -> {});

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}