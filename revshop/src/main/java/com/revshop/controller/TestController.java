package com.revshop.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/buyer")
    public String buyerEndpoint() {
        return "Buyer Access Only";
    }

    @GetMapping("/seller")
    public String sellerEndpoint() {
        return "Seller Access Only";
    }
}