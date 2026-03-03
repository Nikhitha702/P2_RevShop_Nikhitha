package com.revshop.controller;

import com.revshop.entity.Role;
import com.revshop.entity.User;
import com.revshop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void forgotPasswordShouldCreateTokenForExistingUser() throws Exception {
        User user = createUser("buyer1@revshop.com", "secret123");
        userRepository.save(user);

        mockMvc.perform(post("/api/auth/forgot-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"buyer1@revshop.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        User saved = userRepository.findByEmailIgnoreCase("buyer1@revshop.com").orElseThrow();
        assertNotNull(saved.getResetPasswordToken());
        assertNotNull(saved.getResetPasswordTokenExpiry());
        assertTrue(saved.getResetPasswordTokenExpiry().isAfter(LocalDateTime.now().minusMinutes(1)));
    }

    @Test
    void resetPasswordShouldUpdatePasswordAndClearToken() throws Exception {
        User user = createUser("buyer2@revshop.com", "oldPass123");
        user.setResetPasswordToken("token-123");
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        mockMvc.perform(post("/api/auth/reset-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"token-123\",\"newPassword\":\"newPass123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        User saved = userRepository.findByEmailIgnoreCase("buyer2@revshop.com").orElseThrow();
        assertTrue(passwordEncoder.matches("newPass123", saved.getPassword()));
        assertNull(saved.getResetPasswordToken());
        assertNull(saved.getResetPasswordTokenExpiry());
    }

    @Test
    void resetPasswordShouldReturnBadRequestForInvalidToken() throws Exception {
        mockMvc.perform(post("/api/auth/reset-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"invalid\",\"newPassword\":\"newPass123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void forgotPasswordShouldBeRateLimited() throws Exception {
        User user = createUser("ratelimit@revshop.com", "secret123");
        userRepository.save(user);

        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/auth/forgot-password")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"ratelimit@revshop.com\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        mockMvc.perform(post("/api/auth/forgot-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ratelimit@revshop.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void loginShouldReturnJwtTokenForValidCredentials() throws Exception {
        User user = createUser("jwtbuyer@revshop.com", "secret123");
        userRepository.save(user);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"jwtbuyer@revshop.com\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.role").value("ROLE_BUYER"));
    }

    private User createUser(String email, String rawPassword) {
        User user = new User();
        user.setFirstName("Buyer");
        user.setLastName("One");
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setPhone("9876543210");
        user.setAddress("Hyd");
        user.setRole(Role.ROLE_BUYER);
        user.setEnabled(true);
        return user;
    }
}
