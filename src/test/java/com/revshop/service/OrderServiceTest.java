package com.revshop.service;

import com.revshop.dto.ApiResponse;
import com.revshop.dto.CheckoutRequest;
import com.revshop.entity.*;
import com.revshop.repository.CartItemRepository;
import com.revshop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldCheckoutSuccessfully() {
        User buyer = new User();
        buyer.setId(1L);

        User seller = new User();
        seller.setId(2L);

        Product product = new Product();
        product.setId(10L);
        product.setName("Phone");
        product.setActive(true);
        product.setQuantity(8);
        product.setInventoryThreshold(2);
        product.setMrp(BigDecimal.valueOf(500));
        product.setSeller(seller);

        CartItem cartItem = new CartItem();
        cartItem.setBuyer(buyer);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        CheckoutRequest request = new CheckoutRequest();
        request.setShippingAddress("Hyd");
        request.setBillingAddress("Hyd");

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(buyer);
        when(cartItemRepository.findByBuyer(buyer)).thenReturn(List.of(cartItem));

        ApiResponse response = orderService.checkout(request);

        assertEquals(true, response.isSuccess());
        verify(orderRepository).save(any(Order.class));
        verify(cartItemRepository).deleteAll(List.of(cartItem));
        verify(notificationService).createNotification(seller, "New order received for product: Phone");

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        assertEquals(BigDecimal.valueOf(1000), captor.getValue().getTotalAmount());
    }

    @Test
    void shouldRejectCheckoutWhenCartEmpty() {
        User buyer = new User();
        when(currentUserService.getCurrentUserOrThrow()).thenReturn(buyer);
        when(cartItemRepository.findByBuyer(buyer)).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> orderService.checkout(new CheckoutRequest()));
    }

    @Test
    void shouldRejectStatusUpdateFromOtherSeller() {
        User loggedInSeller = new User();
        loggedInSeller.setId(20L);
        User itemSeller = new User();
        itemSeller.setId(10L);
        User buyer = new User();
        buyer.setId(2L);

        Order order = new Order();
        order.setId(9L);
        order.setBuyer(buyer);

        OrderItem item = new OrderItem();
        item.setSeller(itemSeller);
        order.setItems(List.of(item));

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(loggedInSeller);
        when(orderRepository.findById(9L)).thenReturn(Optional.of(order));

        assertThrows(IllegalArgumentException.class, () -> orderService.updateStatus(9L, OrderStatus.SHIPPED));
    }
}
