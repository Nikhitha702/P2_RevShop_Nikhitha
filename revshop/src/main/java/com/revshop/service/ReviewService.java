package com.revshop.service;

import com.revshop.dto.ApiResponse;
import com.revshop.entity.OrderStatus;
import com.revshop.entity.Product;
import com.revshop.entity.Review;
import com.revshop.entity.User;
import com.revshop.repository.OrderRepository;
import com.revshop.repository.ProductRepository;
import com.revshop.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final CurrentUserService currentUserService;
    private final OrderRepository orderRepository;

    public ApiResponse addReview(Long productId, Integer rating, String comment) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        User user = currentUserService.getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        boolean deliveredForProduct = orderRepository
                .existsByUserIdAndStatusAndItemsProductId(user.getId(), OrderStatus.DELIVERED, productId);

        if (!deliveredForProduct) {
            return new ApiResponse(false, "You can review only delivered purchased products");
        }

        if (reviewRepository.existsByProductIdAndUserId(productId, user.getId())) {
            return new ApiResponse(false, "You already reviewed this product");
        }

        Review review = Review.builder()
                .product(product)
                .user(user)
                .rating(rating)
                .comment(comment)
                .createdAt(LocalDateTime.now())
                .build();

        reviewRepository.save(review);
        return new ApiResponse(true, "Review added successfully");
    }

    public List<Review> getProductReviews(Long productId) {
        return reviewRepository.findByProductId(productId);
    }
}
