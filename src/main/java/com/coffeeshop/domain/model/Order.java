package com.coffeeshop.domain.model;

import com.coffeeshop.domain.patterns.state.CancelledState;
import com.coffeeshop.domain.patterns.state.OrderState;
import com.coffeeshop.domain.patterns.state.PaidState;
import com.coffeeshop.domain.patterns.state.PendingState;
import com.coffeeshop.domain.patterns.state.PreparingState;
import com.coffeeshop.domain.patterns.state.ReadyState;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    private final int id;
    private final LocalDateTime createdAt;
    private OrderState state;
    private String discountType = "NONE";
    private double discountAmount;
    private double totalAmount;
    private Payment payment;
    private boolean inventoryDeducted;
    private final List<OrderItem> items = new ArrayList<>();

    public Order(int id) {
        this(id, LocalDateTime.now(), "PENDING", "NONE", 0, 0, false);
    }

    public Order(int id, LocalDateTime createdAt, String status, String discountType, double discountAmount,
                 double totalAmount, boolean inventoryDeducted) {
        this.id = id;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
        this.state = stateFor(status);
        this.discountType = discountType == null || discountType.isBlank() ? "NONE" : discountType;
        this.discountAmount = discountAmount;
        this.totalAmount = totalAmount;
        this.inventoryDeducted = inventoryDeducted;
    }

    public int getId() { return id; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public OrderState getState() { return state; }
    public String getStatus() { return state.getName(); }
    public void setState(OrderState state) { this.state = state; }
    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }
    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }
    public boolean isInventoryDeducted() { return inventoryDeducted; }
    public void setInventoryDeducted(boolean inventoryDeducted) { this.inventoryDeducted = inventoryDeducted; }
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }
    public void addRawItem(OrderItem item) { items.add(item); }
    public void removeRawItem(int itemId) { items.removeIf(item -> item.getId() == itemId); }
    public double getSubtotal() { return items.stream().mapToDouble(OrderItem::getItemPrice).sum(); }

    private static OrderState stateFor(String status) {
        if (status == null) {
            return new PendingState();
        }
        return switch (status.trim().toUpperCase()) {
            case "PREPARING" -> new PreparingState();
            case "READY" -> new ReadyState();
            case "PAID" -> new PaidState();
            case "CANCELLED" -> new CancelledState();
            default -> new PendingState();
        };
    }

    @Override
    public String toString() {
        return "Order #" + id + " - " + getStatus() + " - " + String.format("%,.0f", totalAmount) + "d";
    }
}
