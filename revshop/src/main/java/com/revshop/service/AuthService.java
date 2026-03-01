package com.revshop.service;

import com.revshop.dto.ApiResponse;
import com.revshop.dto.BuyerRegisterRequest;
import com.revshop.dto.SellerRegisterRequest;
import com.revshop.entity.Role;
import com.revshop.entity.User;
import com.revshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ApiResponse registerBuyer(BuyerRegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User buyer = mapCommonFields(request);
        buyer.setRole(Role.ROLE_BUYER);
        userRepository.save(buyer);

        return new ApiResponse(true, "Buyer account created successfully");
    }

    @Transactional
    public ApiResponse registerSeller(SellerRegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User seller = mapCommonFields(request);
        seller.setRole(Role.ROLE_SELLER);
        seller.setBusinessName(request.getBusinessName());
        seller.setGstNumber(request.getGstNumber());
        seller.setBusinessCategory(request.getBusinessCategory());
        userRepository.save(seller);

        return new ApiResponse(true, "Seller account created successfully");
    }

    private User mapCommonFields(BuyerRegisterRequest request) {
        User user = new User();
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone().trim());
        user.setAddress(request.getAddress().trim());
        user.setEnabled(true);
        return user;
    }
}
