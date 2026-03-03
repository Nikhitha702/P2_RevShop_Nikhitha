package com.revshop.service;

import com.revshop.dto.ApiResponse;
import com.revshop.entity.Notification;
import com.revshop.entity.User;
import com.revshop.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public void createNotification(User user, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setReadStatus(false);
        notificationRepository.save(notification);
    }

    public List<Notification> getMyNotifications() {
        return notificationRepository.findByUserOrderByCreatedAtDesc(currentUserService.getCurrentUserOrThrow());
    }

    @Transactional
    public ApiResponse markAsRead(Long id) {
        User user = currentUserService.getCurrentUserOrThrow();
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You can only update your notifications");
        }

        notification.setReadStatus(true);
        notificationRepository.save(notification);
        return new ApiResponse(true, "Notification marked as read");
    }
}
