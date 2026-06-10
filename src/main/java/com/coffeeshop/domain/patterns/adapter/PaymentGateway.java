package com.coffeeshop.domain.patterns.adapter;

public interface PaymentGateway {
    PaymentResult processPayment(double amount);
    String getGatewayName();
}
