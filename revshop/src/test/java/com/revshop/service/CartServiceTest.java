package com.revshop.service;

import com.revshop.dto.ApiResponse;
import com.revshop.entity.Cart;
import com.revshop.entity.Product;
import com.revshop.entity.User;
import com.revshop.repository.CartItemRepository;
import com.revshop.repository.CartRepository;
import com.revshop.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private CartService cartService;

    @Test
    void addToCartShouldRejectInvalidQuantity() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> cartService.addToCart(1L, 0));
        assertEquals("Quantity must be greater than zero", ex.getMessage());
    }

    @Test
    void addToCartShouldReturnSuccessResponse() {
        User user = User.builder().id(1L).build();
        Cart cart = Cart.builder().id(2L).user(user).items(new ArrayList<>()).build();
        Product product = Product.builder()
                .id(3L)
                .price(BigDecimal.valueOf(100))
                .discountedPrice(BigDecimal.valueOf(90))
                .quantity(5)
                .build();

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(productRepository.findById(3L)).thenReturn(Optional.of(product));

        ApiResponse response = cartService.addToCart(3L, 2);

        assertTrue(response.isSuccess());
        assertEquals("Product added to cart", response.getMessage());
        verify(cartItemRepository, times(1)).save(any());
    }
}
