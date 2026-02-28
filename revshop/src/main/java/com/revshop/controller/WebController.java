package com.revshop.controller;

import com.revshop.service.NotificationService;
import com.revshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final ProductService productService;
    private final NotificationService notificationService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("products", productService.getAllProducts(PageRequest.of(0, 12)).getContent());
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("notifications", notificationService.getMyNotifications());
        return "dashboard";
    }
}
