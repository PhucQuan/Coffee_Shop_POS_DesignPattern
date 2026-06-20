package com.coffeeshop.infrastructure;

import java.time.LocalDateTime;

public class OrderStatusHistoryRecord {
    private final int id;
    private final int orderId;
    private final String status;
    private final String note;
    private final LocalDateTime changedAt;

    public OrderStatusHistoryRecord(int id, int orderId, String status, String note, LocalDateTime changedAt) {
        this.id = id;
        this.orderId = orderId;
        this.status = status;
        this.note = note;
        this.changedAt = changedAt;
    }

    public int getId() {
        return id;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }

    public String getNote() {
        return note;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }
}
