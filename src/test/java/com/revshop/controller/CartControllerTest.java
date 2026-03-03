package com.revshop.controller;

import com.revshop.dto.ApiResponse;
import com.revshop.entity.CartItem;
import com.revshop.service.CartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    @Test
    void shouldAddToCart() {
        ApiResponse response = new ApiResponse(true, "Added");
        when(cartService.addToCart(5L, 2)).thenReturn(response);

        ApiResponse actual = cartController.addToCart(5L, 2);

        assertSame(response, actual);
        verify(cartService).addToCart(5L, 2);
    }

    @Test
    void shouldReturnMyCart() {
        CartItem item = new CartItem();
        List<CartItem> items = List.of(item);
        when(cartService.getMyCart()).thenReturn(items);

        List<CartItem> actual = cartController.myCart();

        assertEquals(1, actual.size());
        verify(cartService).getMyCart();
    }

    @Test
    void shouldReturnTotal() {
        when(cartService.totalAmount()).thenReturn(BigDecimal.valueOf(450));

        BigDecimal actual = cartController.total();

        assertEquals(BigDecimal.valueOf(450), actual);
        verify(cartService).totalAmount();
    }
}
