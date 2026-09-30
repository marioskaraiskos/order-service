package com.example.order_service;

import java.time.LocalDateTime;

public class Order {
    private Long orderId;
    private Long productId;
    private String productName;
    private int quantity;
    private double totalPrice;
    private String status;
    private String userId;
    private LocalDateTime createdAt;

    public Order() {
        this.status = "CREATED";
        this.createdAt = LocalDateTime.now();
    }

    public Order(Long orderId, Long productId, String productName, int quantity, double totalPrice) {
        this();
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public Order(Long orderId, Long productId, String productName, int quantity, double totalPrice, String userId) {
        this(orderId, productId, productName, quantity, totalPrice);
        this.userId = userId;
    }

    // --- GETTERS & SETTERS ---
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
