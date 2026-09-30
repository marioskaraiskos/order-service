package com.example.order_service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping({"/api/v1/orders", "/api/orders"})
public class OrderController {

    private final RestTemplate restTemplate;
    private final Map<Long, Order> orderRepository = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(100);

    @Value("${product-service.url:http://product-service/api/v1/products}")
    private String productServiceUrl;

    public OrderController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Integer quantity,
            @RequestBody(required = false) OrderRequest orderRequest,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {

        Long targetProductId = (orderRequest != null && orderRequest.getProductId() != null)
                ? orderRequest.getProductId()
                : productId;

        int targetQuantity = (orderRequest != null && orderRequest.getQuantity() != null)
                ? orderRequest.getQuantity()
                : (quantity != null ? quantity : 1);

        if (targetProductId == null) {
            throw new IllegalArgumentException("Product ID must be provided via query param or request body");
        }

        // 1. Inter-service call: fetch product details
        ProductDTO product = fetchProductDetails(targetProductId);
        if (product == null) {
            throw new RuntimeException("Product not found with ID: " + targetProductId);
        }

        // 2. Compute total price based on product price fetched over HTTP
        double totalPrice = product.getPrice() * targetQuantity;

        // 3. Create and store order
        long newOrderId = idCounter.incrementAndGet();
        Order newOrder = new Order(newOrderId, product.getId(), product.getName(), targetQuantity, totalPrice, userId);
        newOrder.setStatus("CONFIRMED");

        orderRepository.put(newOrderId, newOrder);
        return newOrder;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(
            @PathVariable("id") Long id,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        Order order = orderRepository.get(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public List<Order> getAllOrders(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (userId != null && !userId.isBlank()) {
            return orderRepository.values().stream()
                    .filter(o -> userId.equals(o.getUserId()))
                    .toList();
        }
        return new ArrayList<>(orderRepository.values());
    }

    private ProductDTO fetchProductDetails(Long productId) {
        // Attempt via Eureka LoadBalanced RestTemplate
        try {
            return restTemplate.getForObject(productServiceUrl + "/" + productId, ProductDTO.class);
        } catch (Exception eurekaEx) {
            // Fallback for standalone/local testing without Eureka active
            try {
                RestTemplate standaloneRestTemplate = new RestTemplate();
                return standaloneRestTemplate.getForObject("http://localhost:8082/api/v1/products/" + productId, ProductDTO.class);
            } catch (Exception directEx) {
                throw new RuntimeException("Failed to reach product-service at " + productServiceUrl
                        + " or localhost:8082 for product ID " + productId + ": " + directEx.getMessage(), directEx);
            }
        }
    }
}