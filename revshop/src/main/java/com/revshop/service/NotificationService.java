package com.revshop.service;

import com.revshop.dto.ApiResponse;
import com.revshop.entity.Notification;
import com.revshop.entity.User;
import com.revshop.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final CurrentUserService currentUserService;

    public void createNotification(User user, String message) {
        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .readStatus(false)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
    }

    public List<Notification> getMyNotifications() {
        User user = currentUserService.getCurrentUser();
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public ApiResponse markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        User user = currentUserService.getCurrentUser();
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized notification access");
        }
        notification.setReadStatus(true);
        notificationRepository.save(notification);
        return new ApiResponse(true, "Notification marked as read");
    }
}
