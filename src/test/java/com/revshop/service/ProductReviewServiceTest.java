package com.revshop.service;

import com.revshop.dto.ProductReviewRequest;
import com.revshop.entity.Product;
import com.revshop.entity.User;
import com.revshop.repository.OrderRepository;
import com.revshop.repository.ProductRepository;
import com.revshop.repository.ProductReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductReviewServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductReviewRepository productReviewRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private ProductReviewService productReviewService;

    @Test
    void shouldAddReviewWhenProductPurchased() {
        User buyer = new User();
        buyer.setId(1L);

        Product product = new Product();
        product.setId(3L);

        ProductReviewRequest request = new ProductReviewRequest();
        request.setRating(5);
        request.setReviewText("Excellent quality");

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(buyer);
        when(productRepository.findById(3L)).thenReturn(Optional.of(product));
        when(orderRepository.existsByBuyerAndItemsProductId(buyer, 3L)).thenReturn(true);
        when(productReviewRepository.findByBuyerAndProductId(buyer, 3L)).thenReturn(Optional.empty());

        productReviewService.addOrUpdateReview(3L, request);

        verify(productReviewRepository).save(any());
    }

    @Test
    void shouldRejectReviewWhenProductNotPurchased() {
        User buyer = new User();
        buyer.setId(1L);

        Product product = new Product();
        product.setId(3L);

        ProductReviewRequest request = new ProductReviewRequest();
        request.setRating(4);
        request.setReviewText("Good");

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(buyer);
        when(productRepository.findById(3L)).thenReturn(Optional.of(product));
        when(orderRepository.existsByBuyerAndItemsProductId(buyer, 3L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> productReviewService.addOrUpdateReview(3L, request));
    }
}
