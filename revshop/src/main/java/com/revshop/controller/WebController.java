package com.revshop.controller;

import com.revshop.service.CategoryService;
import com.revshop.service.NotificationService;
import com.revshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final NotificationService notificationService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("categories", categoryService.getAllActiveCategories());
        model.addAttribute("products", productService.browseProducts(PageRequest.of(0, 12)).getContent());
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register/buyer")
    public String registerBuyer() {
        return "register-buyer";
    }

    @GetMapping("/register/seller")
    public String registerSeller() {
        return "register-seller";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        boolean seller = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_SELLER"::equals);

        if (seller) {
            return "redirect:/seller/dashboard";
        }

        return "redirect:/buyer/dashboard";
    }

    @GetMapping("/buyer/dashboard")
    public String buyerDashboard(Model model) {
        model.addAttribute("categories", categoryService.getAllActiveCategories());
        model.addAttribute("products", productService.browseProducts(PageRequest.of(0, 12)).getContent());
        model.addAttribute("notifications", notificationService.getMyNotifications());
        return "buyer-dashboard";
    }

    @GetMapping("/seller/dashboard")
    public String sellerDashboard(Model model) {
        model.addAttribute("categories", categoryService.getAllActiveCategories());
        model.addAttribute("inventory", productService.getSellerInventory());
        model.addAttribute("lowStock", productService.getLowStockProducts());
        model.addAttribute("notifications", notificationService.getMyNotifications());
        return "seller-dashboard";
    }
}
