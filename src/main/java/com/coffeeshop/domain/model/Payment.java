package com.coffeeshop.domain.model;

public class Payment {
    private final int id;
    private final int orderId;
    private final String method;
    private final double amount;
    private final String transactionCode;
    private final String status;

    public Payment(int id, int orderId, String method, double amount, String transactionCode, String status) {
        this.id = id;
        this.orderId = orderId;
        this.method = method;
        this.amount = amount;
        this.transactionCode = transactionCode;
        this.status = status;
    }

    public int getId() { return id; }
    public int getOrderId() { return orderId; }
    public String getMethod() { return method; }
    public double getAmount() { return amount; }
    public String getTransactionCode() { return transactionCode; }
    public String getStatus() { return status; }
}
