package com.revshop.service;

import com.revshop.dto.ApiResponse;
import com.revshop.dto.ProductReviewRequest;
import com.revshop.entity.Product;
import com.revshop.entity.ProductReview;
import com.revshop.entity.User;
import com.revshop.repository.OrderRepository;
import com.revshop.repository.ProductRepository;
import com.revshop.repository.ProductReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductReviewService {

    private final ProductRepository productRepository;
    private final ProductReviewRepository productReviewRepository;
    private final OrderRepository orderRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public ApiResponse addOrUpdateReview(Long productId, ProductReviewRequest request) {
        User buyer = currentUserService.getCurrentUserOrThrow();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        boolean purchased = orderRepository.existsByBuyerAndItemsProductId(buyer, productId);
        if (!purchased) {
            throw new IllegalArgumentException("You can review only purchased products");
        }

        ProductReview review = productReviewRepository.findByBuyerAndProductId(buyer, productId)
                .orElseGet(ProductReview::new);

        review.setBuyer(buyer);
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setReviewText(request.getReviewText().trim());
        productReviewRepository.save(review);

        return new ApiResponse(true, "Review submitted successfully");
    }

    public List<ProductReview> getProductReviews(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("Product not found");
        }
        return productReviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    public List<ProductReview> getSellerProductReviews() {
        return productReviewRepository.findByProductSellerOrderByCreatedAtDesc(currentUserService.getCurrentUserOrThrow());
    }

    public Map<String, Object> ratingSummary(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("Product not found");
        }

        List<ProductReview> reviews = productReviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
        Double avg = productReviewRepository.averageRatingForProduct(productId);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("productId", productId);
        payload.put("averageRating", avg == null ? BigDecimal.ZERO : BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP));
        payload.put("totalReviews", reviews.size());
        payload.put("reviews", reviews);
        return payload;
    }
}
