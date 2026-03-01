package com.revshop.controller;

import com.revshop.dto.ApiResponse;
import com.revshop.entity.Notification;
import com.revshop.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Notification> myNotifications() {
        return notificationService.getMyNotifications();
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse markRead(@PathVariable Long id) {
        return notificationService.markAsRead(id);
    }
}
