package com.revshop.service;

import com.revshop.dto.BuyerRegisterRequest;
import com.revshop.dto.SellerRegisterRequest;
import com.revshop.entity.User;
import com.revshop.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterBuyer() {
        BuyerRegisterRequest req = new BuyerRegisterRequest();
        req.setFirstName("A");
        req.setLastName("B");
        req.setEmail("a@b.com");
        req.setPassword("secret123");
        req.setPhone("9876543210");
        req.setAddress("Hyd");

        when(userRepository.existsByEmailIgnoreCase("a@b.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("ENC_PASS");

        authService.registerBuyer(req);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("a@b.com", captor.getValue().getEmail());
    }

    @Test
    void shouldFailWhenBuyerEmailAlreadyExists() {
        BuyerRegisterRequest req = new BuyerRegisterRequest();
        req.setEmail("a@b.com");

        when(userRepository.existsByEmailIgnoreCase("a@b.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.registerBuyer(req));
    }

    @Test
    void shouldRegisterSeller() {
        SellerRegisterRequest req = new SellerRegisterRequest();
        req.setFirstName("A");
        req.setLastName("B");
        req.setEmail("seller@b.com");
        req.setPassword("secret123");
        req.setPhone("9876543210");
        req.setAddress("Hyd");
        req.setBusinessName("Shop");
        req.setGstNumber("GST123");
        req.setBusinessCategory("Electronics");

        when(userRepository.existsByEmailIgnoreCase("seller@b.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("ENC_PASS");

        authService.registerSeller(req);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("Shop", captor.getValue().getBusinessName());
    }
}
