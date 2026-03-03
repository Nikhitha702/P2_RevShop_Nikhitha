package com.revshop.service;

import com.revshop.dto.ApiResponse;
import com.revshop.dto.BuyerRegisterRequest;
import com.revshop.dto.ForgotPasswordRequest;
import com.revshop.dto.ForgotPasswordResponse;
import com.revshop.dto.LoginRequest;
import com.revshop.dto.LoginResponse;
import com.revshop.dto.ResetPasswordRequest;
import com.revshop.dto.SellerRegisterRequest;
import com.revshop.entity.User;
import com.revshop.mapper.UserMapper;
import com.revshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetRateLimiter passwordResetRateLimiter;
    private final PasswordResetDeliveryService passwordResetDeliveryService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;

    @Transactional
    public ApiResponse registerBuyer(BuyerRegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User buyer = UserMapper.toBuyer(request, passwordEncoder.encode(request.getPassword()));
        userRepository.save(buyer);

        return new ApiResponse(true, "Buyer account created successfully");
    }

    @Transactional
    public ApiResponse registerSeller(SellerRegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User seller = UserMapper.toSeller(request, passwordEncoder.encode(request.getPassword()));
        userRepository.save(seller);

        return new ApiResponse(true, "Seller account created successfully");
    }

    @Transactional
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        passwordResetRateLimiter.validateOrThrow(email);

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("Email is not registered"));

        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);
        passwordResetDeliveryService.sendResetInstructions(user, token);

        return new ForgotPasswordResponse(true, "If your account exists, reset instructions have been sent.");
    }

    @Transactional
    public ApiResponse resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetPasswordToken(request.getToken().trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

        if (user.getResetPasswordTokenExpiry() == null
                || user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            user.setResetPasswordToken(null);
            user.setResetPasswordTokenExpiry(null);
            userRepository.save(user);
            throw new IllegalArgumentException("Invalid or expired reset token");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);
        userRepository.save(user);

        return new ApiResponse(true, "Password reset successful");
    }

    public LoginResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        String token = jwtService.generateToken(userDetails);
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(Object::toString)
                .orElse("ROLE_BUYER");

        return new LoginResponse(
                true,
                "Login successful",
                token,
                "Bearer",
                jwtService.getJwtExpirationSeconds(),
                role
        );
    }

}
