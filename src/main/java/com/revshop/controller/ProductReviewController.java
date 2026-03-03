package com.revshop.controller;

import com.revshop.dto.ApiResponse;
import com.revshop.dto.ProductReviewRequest;
import com.revshop.entity.ProductReview;
import com.revshop.service.ProductReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    @PostMapping("/product/{productId}")
    @PreAuthorize("hasRole('BUYER')")
    public ApiResponse addOrUpdateReview(@PathVariable Long productId,
                                         @Valid @RequestBody ProductReviewRequest request) {
        return productReviewService.addOrUpdateReview(productId, request);
    }

    @GetMapping("/product/{productId}")
    public List<ProductReview> reviewsForProduct(@PathVariable Long productId) {
        return productReviewService.getProductReviews(productId);
    }

    @GetMapping("/product/{productId}/summary")
    public Map<String, Object> productRatingSummary(@PathVariable Long productId) {
        return productReviewService.ratingSummary(productId);
    }

    @GetMapping("/seller")
    @PreAuthorize("hasRole('SELLER')")
    public List<ProductReview> sellerReviews() {
        return productReviewService.getSellerProductReviews();
    }
}
