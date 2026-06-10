package com.coffeeshop.domain.patterns.adapter;

import java.util.Random;
import java.util.UUID;

public class VnpayAdapter implements PaymentGateway {
    private final Random random;

    public VnpayAdapter() {
        this(new Random());
    }

    public VnpayAdapter(Random random) {
        this.random = random;
    }

    public PaymentResult processPayment(double amount) {
        boolean success = random.nextDouble() >= 0.15;
        String code = success ? "VNPAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() : "";
        return new PaymentResult(success, code, success ? "VNPay payment success" : "VNPay payment failed");
    }

    public String getGatewayName() { return "VNPAY"; }
}
