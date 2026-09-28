package com.example.order_service;

public class Order {
    private Long orderId;
    private Long productId;
    private String productName;
    private int quantity;
    private double totalPrice;

    public Order(Long orderId, Long productId, String productName, int quanity, double totalPrice) {
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quanity;
        this.totalPrice = totalPrice;

    }

    // --- GETTERS ARE REQUIRED FOR JSON SERIALIZATION ---
    public Long getOrderId() { return orderId; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getTotalPrice() { return totalPrice; }
}
