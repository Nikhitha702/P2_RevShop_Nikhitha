package com.revshop.service;

import com.revshop.dto.ProductRequest;
import com.revshop.entity.Category;
import com.revshop.entity.Role;
import com.revshop.entity.User;
import com.revshop.repository.CategoryRepository;
import com.revshop.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldAddProduct() {
        User seller = new User();
        seller.setId(1L);
        seller.setRole(Role.ROLE_SELLER);

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        ProductRequest request = new ProductRequest();
        request.setName("Phone");
        request.setDescription("Smart phone");
        request.setMrp(BigDecimal.valueOf(1000));
        request.setDiscountedPrice(BigDecimal.valueOf(950));
        request.setQuantity(5);
        request.setCategoryName("Electronics");

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(seller);
        when(categoryRepository.findByNameIgnoreCase("Electronics")).thenReturn(Optional.of(category));

        productService.addProduct(request);

        verify(productRepository).save(org.mockito.ArgumentMatchers.any());
    }
}
