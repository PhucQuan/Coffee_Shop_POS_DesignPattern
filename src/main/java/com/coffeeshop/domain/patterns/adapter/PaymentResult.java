package com.coffeeshop.domain.patterns.adapter;

public class PaymentResult {
    private final boolean success;
    private final String transactionCode;
    private final String message;

    public PaymentResult(boolean success, String transactionCode, String message) {
        this.success = success;
        this.transactionCode = transactionCode;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public String getTransactionCode() { return transactionCode; }
    public String getMessage() { return message; }
}
