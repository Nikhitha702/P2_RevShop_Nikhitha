package com.revshop.controller;

import com.revshop.dto.ApiResponse;
import com.revshop.entity.Review;
import com.revshop.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('BUYER')")
    public ApiResponse addReview(@RequestParam Long productId,
                            @RequestParam Integer rating,
                            @RequestParam String comment) {
        return reviewService.addReview(productId, rating, comment);
    }

    @GetMapping("/product/{productId}")
    public List<Review> getProductReviews(@PathVariable Long productId) {
        return reviewService.getProductReviews(productId);
    }
}
