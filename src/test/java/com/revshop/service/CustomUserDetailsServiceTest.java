package com.revshop.service;

import com.revshop.entity.Role;
import com.revshop.entity.User;
import com.revshop.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldLoadUserByUsername() {
        User user = new User();
        user.setEmail("seller@test.com");
        user.setPassword("enc-pass");
        user.setRole(Role.ROLE_SELLER);
        user.setEnabled(true);
        when(userRepository.findByEmailIgnoreCase("seller@test.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("seller@test.com");

        assertEquals("seller@test.com", userDetails.getUsername());
        assertEquals("enc-pass", userDetails.getPassword());
    }

    @Test
    void shouldMarkAsDisabledWhenEntityDisabled() {
        User user = new User();
        user.setEmail("buyer@test.com");
        user.setPassword("enc-pass");
        user.setRole(Role.ROLE_BUYER);
        user.setEnabled(false);
        when(userRepository.findByEmailIgnoreCase("buyer@test.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("buyer@test.com");

        assertFalse(userDetails.isEnabled());
    }

    @Test
    void shouldThrowWhenNotFound() {
        when(userRepository.findByEmailIgnoreCase("none@test.com")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("none@test.com"));
    }
}
