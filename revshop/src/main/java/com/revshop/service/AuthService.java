package com.revshop.service;

import com.revshop.entity.Role;
import com.revshop.entity.User;
import com.revshop.repository.RoleRepository;
import com.revshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String register(User user) {

        // ✅ Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "Email already exists!";
        }

        // ✅ Get or create ROLE_BUYER
        Role role = roleRepository.findByName("ROLE_BUYER")
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name("ROLE_BUYER")
                                .build()
                ));

        // ✅ Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // ✅ Set role
        user.setRoles(Collections.singleton(role));

        // ✅ IMPORTANT: Explicitly set enabled
        user.setEnabled(true);

        // ✅ Save user
        userRepository.save(user);

        return "User Registered Successfully";
    }

    public String login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return "Invalid Password";
        }

        return "Login Successful";
    }
}