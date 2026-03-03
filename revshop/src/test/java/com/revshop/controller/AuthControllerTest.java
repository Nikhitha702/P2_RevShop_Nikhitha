package com.revshop.controller;

import com.revshop.dto.ApiResponse;
import com.revshop.dto.BuyerRegisterRequest;
import com.revshop.dto.ForgotPasswordRequest;
import com.revshop.dto.ForgotPasswordResponse;
import com.revshop.dto.ResetPasswordRequest;
import com.revshop.dto.SellerRegisterRequest;
import com.revshop.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void shouldRegisterBuyer() {
        BuyerRegisterRequest request = new BuyerRegisterRequest();
        ApiResponse response = new ApiResponse(true, "Buyer registered");
        when(authService.registerBuyer(request)).thenReturn(response);

        ApiResponse actual = authController.registerBuyer(request);

        assertSame(response, actual);
        verify(authService).registerBuyer(request);
    }

    @Test
    void shouldRegisterSeller() {
        SellerRegisterRequest request = new SellerRegisterRequest();
        ApiResponse response = new ApiResponse(true, "Seller registered");
        when(authService.registerSeller(request)).thenReturn(response);

        ApiResponse actual = authController.registerSeller(request);

        assertSame(response, actual);
        verify(authService).registerSeller(request);
    }

    @Test
    void shouldRequestForgotPassword() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        ForgotPasswordResponse response = new ForgotPasswordResponse(true, "Reset requested", "token-123");
        when(authService.forgotPassword(request)).thenReturn(response);

        ForgotPasswordResponse actual = authController.forgotPassword(request);

        assertSame(response, actual);
        verify(authService).forgotPassword(request);
    }

    @Test
    void shouldResetPassword() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        ApiResponse response = new ApiResponse(true, "Password reset");
        when(authService.resetPassword(request)).thenReturn(response);

        ApiResponse actual = authController.resetPassword(request);

        assertSame(response, actual);
        verify(authService).resetPassword(request);
    }
}
