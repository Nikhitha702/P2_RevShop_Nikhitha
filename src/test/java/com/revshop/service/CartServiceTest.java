package com.revshop.service;

import com.revshop.entity.Product;
import com.revshop.entity.User;
import com.revshop.repository.CartItemRepository;
import com.revshop.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private CartService cartService;

    @Test
    void shouldAddToCart() {
        User buyer = new User();
        buyer.setId(1L);

        Product product = new Product();
        product.setId(5L);
        product.setActive(true);
        product.setQuantity(10);
        product.setMrp(BigDecimal.valueOf(100));

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(buyer);
        when(productRepository.findById(5L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByBuyerAndProductId(buyer, 5L)).thenReturn(Optional.empty());

        cartService.addToCart(5L, 2);

        verify(cartItemRepository).save(any());
    }

    @Test
    void shouldRejectWhenTotalCartQuantityExceedsStock() {
        User buyer = new User();
        buyer.setId(1L);

        Product product = new Product();
        product.setId(7L);
        product.setActive(true);
        product.setQuantity(3);
        product.setMrp(BigDecimal.valueOf(250));

        com.revshop.entity.CartItem existing = new com.revshop.entity.CartItem();
        existing.setQuantity(2);
        existing.setProduct(product);
        existing.setBuyer(buyer);

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(buyer);
        when(productRepository.findById(7L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByBuyerAndProductId(buyer, 7L)).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () -> cartService.addToCart(7L, 2));
    }
}
