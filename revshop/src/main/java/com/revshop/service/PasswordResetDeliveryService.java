package com.revshop.service;

import com.revshop.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class PasswordResetDeliveryService {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final String fromAddress;
    private final String resetBaseUrl;
    private final String mailHost;

    public PasswordResetDeliveryService(
            ObjectProvider<JavaMailSender> mailSenderProvider,
            @Value("${app.mail.from:no-reply@revshop.local}") String fromAddress,
            @Value("${app.reset-password.base-url:http://localhost:8080/reset-password}") String resetBaseUrl,
            @Value("${spring.mail.host:}") String mailHost) {
        this.mailSenderProvider = mailSenderProvider;
        this.fromAddress = fromAddress;
        this.resetBaseUrl = resetBaseUrl;
        this.mailHost = mailHost;
    }

    public void sendResetInstructions(User user, String token) {
        if (mailHost == null || mailHost.isBlank()) {
            log.warn("Password reset requested for user id {} but spring.mail.host is not configured", user.getId());
            return;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.warn("Password reset requested for user id {} but mail sender is not configured", user.getId());
            return;
        }

        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        String resetLink = resetBaseUrl + "?resetToken=" + encodedToken;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(user.getEmail());
        message.setSubject("RevShop Password Reset");
        message.setText("Use this link to reset your password: " + resetLink + "\nThis link expires in 30 minutes.");

        mailSender.send(message);
    }
}
