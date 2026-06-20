package com.coffeeshop.infrastructure;

import java.time.LocalDateTime;

public class InventoryTransactionRecord {
    private final int id;
    private final int inventoryItemId;
    private final String inventoryItemName;
    private final Integer orderId;
    private final double changeAmount;
    private final double balanceAfter;
    private final String reason;
    private final LocalDateTime createdAt;

    public InventoryTransactionRecord(int id, int inventoryItemId, String inventoryItemName, Integer orderId,
                                      double changeAmount, double balanceAfter, String reason, LocalDateTime createdAt) {
        this.id = id;
        this.inventoryItemId = inventoryItemId;
        this.inventoryItemName = inventoryItemName;
        this.orderId = orderId;
        this.changeAmount = changeAmount;
        this.balanceAfter = balanceAfter;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public int getInventoryItemId() {
        return inventoryItemId;
    }

    public String getInventoryItemName() {
        return inventoryItemName;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public double getChangeAmount() {
        return changeAmount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
