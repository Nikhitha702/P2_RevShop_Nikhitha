package com.revshop.service;

import com.revshop.entity.Notification;
import com.revshop.entity.User;
import com.revshop.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void shouldCreateNotification() {
        User user = new User();
        user.setId(10L);

        notificationService.createNotification(user, "Order placed");

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertEquals("Order placed", captor.getValue().getMessage());
    }
}
