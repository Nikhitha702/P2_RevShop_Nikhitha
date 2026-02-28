package com.revshop.service;

import com.revshop.dto.ApiResponse;
import com.revshop.entity.Favorite;
import com.revshop.entity.Product;
import com.revshop.entity.User;
import com.revshop.repository.FavoriteRepository;
import com.revshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;
    private final CurrentUserService currentUserService;

    public ApiResponse addFavorite(Long productId) {
        User user = currentUserService.getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (favoriteRepository.findByUserIdAndProductId(user.getId(), productId).isPresent()) {
            return new ApiResponse(false, "Product already in favorites");
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .product(product)
                .createdAt(LocalDateTime.now())
                .build();
        favoriteRepository.save(favorite);
        return new ApiResponse(true, "Product added to favorites");
    }

    public ApiResponse removeFavorite(Long productId) {
        User user = currentUserService.getCurrentUser();
        Favorite favorite = favoriteRepository.findByUserIdAndProductId(user.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Favorite not found"));
        favoriteRepository.delete(favorite);
        return new ApiResponse(true, "Product removed from favorites");
    }

    public List<Favorite> getMyFavorites() {
        User user = currentUserService.getCurrentUser();
        return favoriteRepository.findByUserOrderByCreatedAtDesc(user);
    }
}
