package com.revshop.service;

import com.revshop.entity.Order;
import com.revshop.entity.Payment;
import com.revshop.entity.PaymentMethod;
import com.revshop.entity.User;
import com.revshop.repository.OrderRepository;
import com.revshop.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void shouldPreventDuplicatePayment() {
        User buyer = new User();
        buyer.setId(1L);

        Order order = new Order();
        order.setId(10L);
        order.setBuyer(buyer);

        Payment existing = new Payment();
        existing.setOrder(order);

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(buyer);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrder(order)).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () -> paymentService.pay(10L, PaymentMethod.CREDIT_CARD));
    }

    @Test
    void shouldPayOrderSuccessfully() {
        User buyer = new User();
        buyer.setId(2L);

        Order order = new Order();
        order.setId(20L);
        order.setBuyer(buyer);

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(buyer);
        when(orderRepository.findById(20L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrder(order)).thenReturn(Optional.empty());

        paymentService.pay(20L, PaymentMethod.DEBIT_CARD);

        verify(paymentRepository).save(any(Payment.class));
    }
}
