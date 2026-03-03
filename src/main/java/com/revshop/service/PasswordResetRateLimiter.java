package com.revshop.service;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PasswordResetRateLimiter {

    private static final int MAX_REQUESTS = 5;
    private static final int WINDOW_MINUTES = 15;

    private final Map<String, AttemptWindow> attemptsByEmail = new ConcurrentHashMap<>();

    public void validateOrThrow(String email) {
        LocalDateTime now = LocalDateTime.now();
        String key = email.trim().toLowerCase();

        AttemptWindow current = attemptsByEmail.compute(key, (k, v) -> {
            if (v == null || v.windowStart().plusMinutes(WINDOW_MINUTES).isBefore(now)) {
                return new AttemptWindow(1, now);
            }
            return new AttemptWindow(v.count() + 1, v.windowStart());
        });

        if (current.count() > MAX_REQUESTS) {
            throw new IllegalArgumentException("Too many password reset requests. Please try again in 15 minutes.");
        }
    }

    private record AttemptWindow(int count, LocalDateTime windowStart) {
    }
}
