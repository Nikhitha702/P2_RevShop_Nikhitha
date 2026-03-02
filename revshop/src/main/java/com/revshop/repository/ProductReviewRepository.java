package com.revshop.repository;

import com.revshop.entity.ProductReview;
import com.revshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    Optional<ProductReview> findByBuyerAndProductId(User buyer, Long productId);
    List<ProductReview> findByProductIdOrderByCreatedAtDesc(Long productId);
    List<ProductReview> findByProductSellerOrderByCreatedAtDesc(User seller);

    @Query("select avg(pr.rating) from ProductReview pr where pr.product.id = :productId")
    Double averageRatingForProduct(Long productId);
}
