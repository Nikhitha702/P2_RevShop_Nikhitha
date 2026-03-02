package com.revshop.service;

import com.revshop.entity.Product;
import com.revshop.entity.User;
import com.revshop.repository.FavoriteRepository;
import com.revshop.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private FavoriteService favoriteService;

    @Test
    void shouldAddFavorite() {
        User buyer = new User();
        buyer.setId(1L);

        Product product = new Product();
        product.setId(5L);
        product.setActive(true);

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(buyer);
        when(productRepository.findById(5L)).thenReturn(Optional.of(product));
        when(favoriteRepository.findByBuyerAndProductId(buyer, 5L)).thenReturn(Optional.empty());

        favoriteService.addFavorite(5L);

        verify(favoriteRepository).save(any());
    }
}
