package com.revshop.controller;

import com.revshop.dto.ApiResponse;
import com.revshop.entity.Favorite;
import com.revshop.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{productId}")
    @PreAuthorize("hasRole('BUYER')")
    public ApiResponse addFavorite(@PathVariable Long productId) {
        return favoriteService.addFavorite(productId);
    }

    @GetMapping
    @PreAuthorize("hasRole('BUYER')")
    public List<Favorite> myFavorites() {
        return favoriteService.myFavorites();
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('BUYER')")
    public ApiResponse removeFavorite(@PathVariable Long productId) {
        return favoriteService.removeFavorite(productId);
    }
}
