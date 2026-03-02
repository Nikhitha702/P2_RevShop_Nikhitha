package com.revshop.service;

import com.revshop.dto.ApiResponse;
import com.revshop.entity.Favorite;
import com.revshop.entity.Product;
import com.revshop.entity.User;
import com.revshop.repository.FavoriteRepository;
import com.revshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public ApiResponse addFavorite(Long productId) {
        User buyer = currentUserService.getCurrentUserOrThrow();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (!product.isActive()) {
            throw new IllegalArgumentException("Product is not available");
        }

        if (favoriteRepository.findByBuyerAndProductId(buyer, productId).isPresent()) {
            return new ApiResponse(true, "Product is already in favorites");
        }

        Favorite favorite = new Favorite();
        favorite.setBuyer(buyer);
        favorite.setProduct(product);
        favoriteRepository.save(favorite);
        return new ApiResponse(true, "Product added to favorites");
    }

    public List<Favorite> myFavorites() {
        return favoriteRepository.findByBuyerOrderByCreatedAtDesc(currentUserService.getCurrentUserOrThrow());
    }

    @Transactional
    public ApiResponse removeFavorite(Long productId) {
        User buyer = currentUserService.getCurrentUserOrThrow();
        Favorite favorite = favoriteRepository.findByBuyerAndProductId(buyer, productId)
                .orElseThrow(() -> new IllegalArgumentException("Favorite item not found"));
        favoriteRepository.delete(favorite);
        return new ApiResponse(true, "Product removed from favorites");
    }
}
