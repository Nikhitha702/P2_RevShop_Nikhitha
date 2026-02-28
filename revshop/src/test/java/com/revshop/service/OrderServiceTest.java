package com.revshop.service;

import com.revshop.dto.ApiResponse;
import com.revshop.entity.Order;
import com.revshop.entity.OrderItem;
import com.revshop.entity.OrderStatus;
import com.revshop.entity.Product;
import com.revshop.entity.Seller;
import com.revshop.entity.User;
import com.revshop.repository.CartItemRepository;
import com.revshop.repository.CartRepository;
import com.revshop.repository.OrderItemRepository;
import com.revshop.repository.OrderRepository;
import com.revshop.repository.ProductRepository;
import com.revshop.repository.SellerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void updateOrderStatusShouldRejectUnauthorizedSeller() {
        User user = User.builder().id(1L).build();
        User anotherUser = User.builder().id(2L).build();
        Seller seller = Seller.builder().id(10L).user(user).build();
        Seller otherSeller = Seller.builder().id(20L).user(anotherUser).build();
        Product product = Product.builder().id(30L).seller(otherSeller).build();
        OrderItem item = OrderItem.builder().product(product).build();
        Order order = Order.builder().id(40L).items(List.of(item)).status(OrderStatus.PLACED).build();

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(sellerRepository.findByUserId(1L)).thenReturn(Optional.of(seller));
        when(orderRepository.findById(40L)).thenReturn(Optional.of(order));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> orderService.updateOrderStatus(40L, OrderStatus.SHIPPED));

        assertEquals("Unauthorized order access", ex.getMessage());
    }

    @Test
    void updateOrderStatusShouldSucceedForOwnedItem() {
        User user = User.builder().id(1L).build();
        Seller seller = Seller.builder().id(10L).user(user).build();
        Product product = Product.builder().id(30L).seller(seller).build();
        OrderItem item = OrderItem.builder().product(product).build();
        Order order = Order.builder().id(40L).items(List.of(item)).status(OrderStatus.PLACED).user(user).build();

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(sellerRepository.findByUserId(1L)).thenReturn(Optional.of(seller));
        when(orderRepository.findById(40L)).thenReturn(Optional.of(order));

        ApiResponse response = orderService.updateOrderStatus(40L, OrderStatus.SHIPPED);

        assertTrue(response.isSuccess());
        assertEquals("Order status updated successfully", response.getMessage());
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        verify(orderRepository, times(1)).save(order);
    }
}
