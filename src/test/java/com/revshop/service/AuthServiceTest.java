package com.revshop.service;

import com.revshop.dto.BuyerRegisterRequest;
import com.revshop.dto.ForgotPasswordRequest;
import com.revshop.dto.ForgotPasswordResponse;
import com.revshop.dto.LoginRequest;
import com.revshop.dto.LoginResponse;
import com.revshop.dto.ResetPasswordRequest;
import com.revshop.dto.SellerRegisterRequest;
import com.revshop.entity.User;
import com.revshop.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordResetRateLimiter passwordResetRateLimiter;
    @Mock
    private PasswordResetDeliveryService passwordResetDeliveryService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private CustomUserDetailsService customUserDetailsService;
    @Mock
    private JwtService jwtService;

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

    @Test
    void shouldGenerateForgotPasswordTokenWhenUserExists() {
        ForgotPasswordRequest req = new ForgotPasswordRequest();
        req.setEmail("a@b.com");
        User user = new User();
        user.setEmail("a@b.com");

        when(userRepository.findByEmailIgnoreCase("a@b.com")).thenReturn(Optional.of(user));

        ForgotPasswordResponse response = authService.forgotPassword(req);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        verify(passwordResetRateLimiter).validateOrThrow("a@b.com");
        verify(passwordResetDeliveryService).sendResetInstructions(any(User.class), any(String.class));
        User saved = captor.getValue();
        assertTrue(response.isSuccess());
        assertTrue(saved.getResetPasswordToken() != null && !saved.getResetPasswordToken().isBlank());
        assertTrue(saved.getResetPasswordTokenExpiry() != null);
    }

    @Test
    void shouldReplaceExistingForgotPasswordToken() {
        ForgotPasswordRequest req = new ForgotPasswordRequest();
        req.setEmail("a@b.com");

        User user = new User();
        user.setEmail("a@b.com");
        user.setResetPasswordToken("old-token");
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusMinutes(5));
        when(userRepository.findByEmailIgnoreCase("a@b.com")).thenReturn(Optional.of(user));

        authService.forgotPassword(req);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertTrue(saved.getResetPasswordToken() != null && !saved.getResetPasswordToken().isBlank());
        assertTrue(!"old-token".equals(saved.getResetPasswordToken()));
    }

    @Test
    void shouldResetPasswordWithValidToken() {
        ResetPasswordRequest req = new ResetPasswordRequest();
        req.setToken("valid-token");
        req.setNewPassword("newSecret123");

        User user = new User();
        user.setResetPasswordToken("valid-token");
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusMinutes(10));

        when(userRepository.findByResetPasswordToken("valid-token")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newSecret123")).thenReturn("ENC_NEW_PASS");

        authService.resetPassword(req);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertEquals("ENC_NEW_PASS", saved.getPassword());
        assertNull(saved.getResetPasswordToken());
        assertNull(saved.getResetPasswordTokenExpiry());
    }

    @Test
    void shouldReturnGenericResponseForUnknownForgotPasswordEmail() {
        ForgotPasswordRequest req = new ForgotPasswordRequest();
        req.setEmail("missing@b.com");
        when(userRepository.findByEmailIgnoreCase("missing@b.com")).thenReturn(Optional.empty());

        ForgotPasswordResponse response = authService.forgotPassword(req);

        assertTrue(response.isSuccess());
        assertEquals("If your account exists, reset instructions have been sent.", response.getMessage());
        verify(passwordResetRateLimiter).validateOrThrow("missing@b.com");
        verifyNoInteractions(passwordEncoder, passwordResetDeliveryService);
    }

    @Test
    void shouldFailResetPasswordWhenTokenExpired() {
        ResetPasswordRequest req = new ResetPasswordRequest();
        req.setToken("expired-token");
        req.setNewPassword("newSecret123");

        User user = new User();
        user.setResetPasswordToken("expired-token");
        user.setResetPasswordTokenExpiry(LocalDateTime.now().minusMinutes(1));

        when(userRepository.findByResetPasswordToken("expired-token")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> authService.resetPassword(req));
    }

    @Test
    void shouldFailForgotPasswordWhenRateLimitExceeded() {
        ForgotPasswordRequest req = new ForgotPasswordRequest();
        req.setEmail("a@b.com");
        doThrow(new IllegalArgumentException("Too many password reset requests. Please try again in 15 minutes."))
                .when(passwordResetRateLimiter).validateOrThrow("a@b.com");

        assertThrows(IllegalArgumentException.class, () -> authService.forgotPassword(req));
        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldLoginAndReturnJwtToken() {
        LoginRequest req = new LoginRequest();
        req.setEmail("buyer@revshop.com");
        req.setPassword("secret123");

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("buyer@revshop.com")
                .password("ENC_PASS")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_BUYER")))
                .build();

        when(customUserDetailsService.loadUserByUsername("buyer@revshop.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");
        when(jwtService.getJwtExpirationSeconds()).thenReturn(3600L);

        LoginResponse response = authService.login(req);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertTrue(response.isSuccess());
        assertEquals("jwt-token", response.getToken());
        assertEquals("ROLE_BUYER", response.getRole());
    }

    @Test
    void shouldFailLoginForInvalidCredentials() {
        LoginRequest req = new LoginRequest();
        req.setEmail("buyer@revshop.com");
        req.setPassword("wrong");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(IllegalArgumentException.class, () -> authService.login(req));
    }
}
