package com.revshop.controller;

import com.revshop.entity.Favorite;
import com.revshop.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@PreAuthorize("hasRole('BUYER')")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{productId}")
    public String addFavorite(@PathVariable Long productId) {
        return favoriteService.addFavorite(productId);
    }

    @DeleteMapping("/{productId}")
    public String removeFavorite(@PathVariable Long productId) {
        return favoriteService.removeFavorite(productId);
    }

    @GetMapping
    public List<Favorite> getMyFavorites() {
        return favoriteService.getMyFavorites();
    }
}
