package com.revshop.service;

import com.revshop.entity.Product;
import com.revshop.entity.Review;
import com.revshop.entity.User;
import com.revshop.repository.OrderRepository;
import com.revshop.repository.ProductRepository;
import com.revshop.repository.ReviewRepository;
import com.revshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public String addReview(Long productId, Integer rating, String comment) {

        // 🔹 Get logged-in user email
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 🔹 Check if user has at least one DELIVERED order
        boolean delivered = orderRepository
                .findByUserId(user.getId())
                .stream()
                .anyMatch(order ->
                        order.getStatus() != null &&
                                order.getStatus().toString().equalsIgnoreCase("DELIVERED")
                );

        if (!delivered) {
            return "You can review only delivered products";
        }

        // 🔹 Prevent duplicate review
        if (reviewRepository.existsByProductIdAndUserId(productId, user.getId())) {
            return "You already reviewed this product";
        }

        // 🔹 Save review
        Review review = Review.builder()
                .product(product)
                .user(user)
                .rating(rating)
                .comment(comment)
                .createdAt(LocalDateTime.now())
                .build();

        reviewRepository.save(review);

        return "Review added successfully";
    }

    public List<Review> getProductReviews(Long productId) {
        return reviewRepository.findByProductId(productId);
    }
}