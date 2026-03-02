package com.revshop.controller;

import com.revshop.dto.ApiResponse;
import com.revshop.dto.ProductReviewRequest;
import com.revshop.entity.ProductReview;
import com.revshop.service.ProductReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductReviewControllerTest {

    @Mock
    private ProductReviewService productReviewService;

    @InjectMocks
    private ProductReviewController productReviewController;

    @Test
    void shouldAddOrUpdateReview() {
        ProductReviewRequest request = new ProductReviewRequest();
        request.setRating(5);
        request.setReviewText("Excellent");
        ApiResponse response = new ApiResponse(true, "Review saved");
        when(productReviewService.addOrUpdateReview(9L, request)).thenReturn(response);

        ApiResponse actual = productReviewController.addOrUpdateReview(9L, request);

        assertSame(response, actual);
        verify(productReviewService).addOrUpdateReview(9L, request);
    }

    @Test
    void shouldReturnProductSummary() {
        Map<String, Object> summary = Map.of("averageRating", 4.5, "totalReviews", 2);
        when(productReviewService.ratingSummary(9L)).thenReturn(summary);

        Map<String, Object> actual = productReviewController.productRatingSummary(9L);

        assertEquals(4.5, actual.get("averageRating"));
        verify(productReviewService).ratingSummary(9L);
    }

    @Test
    void shouldReturnSellerReviews() {
        ProductReview review = new ProductReview();
        when(productReviewService.getSellerProductReviews()).thenReturn(List.of(review));

        List<ProductReview> actual = productReviewController.sellerReviews();

        assertEquals(1, actual.size());
        verify(productReviewService).getSellerProductReviews();
    }
}
