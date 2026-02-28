package com.revshop.service;

import com.revshop.dto.ApiResponse;
import com.revshop.entity.OrderStatus;
import com.revshop.entity.Product;
import com.revshop.entity.User;
import com.revshop.repository.OrderRepository;
import com.revshop.repository.ProductRepository;
import com.revshop.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void addReviewShouldRejectWhenProductNotDeliveredForUser() {
        User buyer = User.builder().id(3L).build();
        Product product = Product.builder().id(9L).name("Phone").build();

        when(currentUserService.getCurrentUser()).thenReturn(buyer);
        when(productRepository.findById(9L)).thenReturn(Optional.of(product));
        when(orderRepository.existsByUserIdAndStatusAndItemsProductId(3L, OrderStatus.DELIVERED, 9L)).thenReturn(false);

        ApiResponse response = reviewService.addReview(9L, 5, "Great");

        assertEquals("You can review only delivered purchased products", response.getMessage());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void addReviewShouldRejectInvalidRating() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reviewService.addReview(9L, 0, "Bad input"));
        assertEquals("Rating must be between 1 and 5", ex.getMessage());
    }
}
