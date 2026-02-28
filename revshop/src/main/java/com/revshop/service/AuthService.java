package com.revshop.service;

import com.revshop.dto.BuyerRegisterRequest;
import com.revshop.dto.SellerRegisterRequest;
import com.revshop.entity.Role;
import com.revshop.entity.Seller;
import com.revshop.entity.User;
import com.revshop.repository.RoleRepository;
import com.revshop.repository.SellerRepository;
import com.revshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;

    // 🛍 Buyer Registration
    public String registerBuyer(BuyerRegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return "Email already exists!";
        }

        Role role = roleRepository.findByName("ROLE_BUYER")
                .orElseGet(() ->
                        roleRepository.save(Role.builder()
                                .name("ROLE_BUYER")
                                .build())
                );

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .address(request.getAddress())
                .createdAt(LocalDateTime.now())
                .enabled(true)
                .roles(Collections.singleton(role))
                .build();

        userRepository.save(user);

        return "Buyer Registered Successfully";
    }

    public String registerSeller(SellerRegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return "Email already exists!";
        }

        Role role = roleRepository.findByName("ROLE_SELLER")
                .orElseGet(() ->
                        roleRepository.save(Role.builder()
                                .name("ROLE_SELLER")
                                .build())
                );

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .address(request.getAddress())
                .createdAt(LocalDateTime.now())
                .enabled(true)
                .roles(Collections.singleton(role))
                .build();

        userRepository.save(user);

        Seller seller = Seller.builder()
                .user(user)
                .businessName(request.getBusinessName())
                .gstNumber(request.getGstNumber())
                .address(request.getAddress())
                .phone(request.getPhone())
                .category(request.getCategory())
                .createdAt(LocalDateTime.now())
                .build();

        sellerRepository.save(seller);

        return "Seller Registered Successfully";
    }
}
