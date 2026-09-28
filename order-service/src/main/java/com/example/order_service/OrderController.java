package com.example.order_service;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final RestTemplate restTemplate;

    public OrderController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping
    public Order createOrder(@RequestParam Long productId, @RequestParam int quantity) {
        // 1. Inter-service call: Call Product Service running on port 8080
        String productServiceUrl = "http://localhost:8080/api/products/" + productId;
        ProductDTO product = restTemplate.getForObject(productServiceUrl, ProductDTO.class);

        if (product == null) {
            throw new RuntimeException("Product not found with ID: " + productId);
        }

        // 2. Compute total price based on real product price fetched over HTTP
        double totalPrice = product.getPrice() * quantity;

        // 3. Return the generated order summary
        return new Order(101L, product.getId(), product.getName(), quantity, totalPrice);
    }
}