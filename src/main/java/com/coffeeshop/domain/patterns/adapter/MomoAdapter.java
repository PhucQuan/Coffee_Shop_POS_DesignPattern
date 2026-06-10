package com.coffeeshop.domain.patterns.adapter;

import java.util.Random;
import java.util.UUID;

public class MomoAdapter implements PaymentGateway {
    private final Random random;

    public MomoAdapter() {
        this(new Random());
    }

    public MomoAdapter(Random random) {
        this.random = random;
    }

    public PaymentResult processPayment(double amount) {
        boolean success = random.nextDouble() >= 0.15;
        String code = success ? "MOMO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() : "";
        return new PaymentResult(success, code, success ? "Momo payment success" : "Momo payment failed");
    }

    public String getGatewayName() { return "MOMO"; }
}
