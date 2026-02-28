package com.revshop.service;

import com.revshop.entity.Order;
import com.revshop.entity.OrderStatus;
import com.revshop.entity.PaymentMethod;
import com.revshop.entity.User;
import com.revshop.repository.OrderRepository;
import com.revshop.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void makePaymentShouldRejectWhenOrderBelongsToAnotherUser() {
        User current = User.builder().id(1L).build();
        User owner = User.builder().id(2L).build();
        Order order = Order.builder().id(10L).user(owner).status(OrderStatus.PLACED).totalAmount(BigDecimal.TEN).build();

        when(currentUserService.getCurrentUser()).thenReturn(current);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> paymentService.makePayment(10L, PaymentMethod.CREDIT_CARD));

        assertEquals("Unauthorized payment access", ex.getMessage());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void makePaymentShouldSetShippedForCashOnDelivery() {
        User buyer = User.builder().id(1L).build();
        Order order = Order.builder().id(11L).user(buyer).status(OrderStatus.PLACED).totalAmount(BigDecimal.TEN).build();

        when(currentUserService.getCurrentUser()).thenReturn(buyer);
        when(orderRepository.findById(11L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderId(11L)).thenReturn(Optional.empty());

        String response = paymentService.makePayment(11L, PaymentMethod.CASH_ON_DELIVERY);

        assertEquals("Payment Successful", response);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        verify(paymentRepository, times(1)).save(any());
        verify(orderRepository, times(1)).save(order);
    }
}
