package com.revshop.controller;

import com.revshop.config.SecurityConfig;
import com.revshop.dto.ApiResponse;
import com.revshop.entity.PaymentMethod;
import com.revshop.service.CartService;
import com.revshop.service.OrderService;
import com.revshop.service.PaymentService;
import com.revshop.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ProductController.class, CartController.class, OrderController.class, PaymentController.class})
@Import(SecurityConfig.class)
class RoleAccessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    @WithMockUser(roles = "BUYER")
    void addProductShouldBeForbiddenForBuyer() throws Exception {
        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Phone\",\"description\":\"Smartphone\",\"categoryName\":\"Electronics\",\"price\":1000,\"discountedPrice\":900,\"quantity\":5}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SELLER")
    void addProductShouldSucceedForSeller() throws Exception {
        when(productService.addProduct(any()))
                .thenReturn(new ApiResponse(true, "Product Added Successfully"));

        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Phone\",\"description\":\"Smartphone\",\"categoryName\":\"Electronics\",\"price\":1000,\"discountedPrice\":900,\"quantity\":5}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "SELLER")
    void addToCartShouldBeForbiddenForSeller() throws Exception {
        mockMvc.perform(post("/api/cart/add")
                        .with(csrf())
                        .param("productId", "1")
                        .param("quantity", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "BUYER")
    void addToCartShouldSucceedForBuyer() throws Exception {
        when(cartService.addToCart(anyLong(), any())).thenReturn(new ApiResponse(true, "Product added to cart"));

        mockMvc.perform(post("/api/cart/add")
                        .with(csrf())
                        .param("productId", "1")
                        .param("quantity", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "BUYER")
    void updateOrderStatusShouldBeForbiddenForBuyer() throws Exception {
        mockMvc.perform(put("/api/orders/update-status")
                        .with(csrf())
                        .param("orderId", "1")
                        .param("status", "SHIPPED"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SELLER")
    void updateOrderStatusShouldSucceedForSeller() throws Exception {
        when(orderService.updateOrderStatus(anyLong(), any()))
                .thenReturn(new ApiResponse(true, "Order status updated successfully"));

        mockMvc.perform(put("/api/orders/update-status")
                        .with(csrf())
                        .param("orderId", "1")
                        .param("status", "SHIPPED"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "SELLER")
    void paymentShouldBeForbiddenForSeller() throws Exception {
        mockMvc.perform(post("/api/payments/pay")
                        .with(csrf())
                        .param("orderId", "1")
                        .param("method", PaymentMethod.CREDIT_CARD.name()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "BUYER")
    void paymentShouldSucceedForBuyer() throws Exception {
        when(paymentService.makePayment(anyLong(), any()))
                .thenReturn(new ApiResponse(true, "Payment Successful"));

        mockMvc.perform(post("/api/payments/pay")
                        .with(csrf())
                        .param("orderId", "1")
                        .param("method", PaymentMethod.CREDIT_CARD.name()))
                .andExpect(status().isOk());
    }
}
