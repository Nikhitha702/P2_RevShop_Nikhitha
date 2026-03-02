package com.revshop.service;

import com.revshop.entity.User;
import com.revshop.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrentUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CurrentUserService currentUserService;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnCurrentUser() {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("buyer@test.com", "pwd"));
        User user = new User();
        user.setEmail("buyer@test.com");
        when(userRepository.findByEmailIgnoreCase("buyer@test.com")).thenReturn(Optional.of(user));

        User actual = currentUserService.getCurrentUserOrThrow();

        assertEquals("buyer@test.com", actual.getEmail());
    }

    @Test
    void shouldThrowWhenUserMissing() {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("missing@test.com", "pwd"));
        when(userRepository.findByEmailIgnoreCase("missing@test.com")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> currentUserService.getCurrentUserOrThrow());
    }
}
