package com.revshop.repository;

import com.revshop.entity.Favorite;
import com.revshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByBuyerAndProductId(User buyer, Long productId);
    List<Favorite> findByBuyerOrderByCreatedAtDesc(User buyer);
}
