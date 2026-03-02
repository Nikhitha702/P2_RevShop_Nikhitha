package com.revshop.service;

import com.revshop.dto.ApiResponse;
import com.revshop.dto.BuyerRegisterRequest;
import com.revshop.dto.SellerRegisterRequest;
import com.revshop.entity.User;
import com.revshop.mapper.UserMapper;
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

}
