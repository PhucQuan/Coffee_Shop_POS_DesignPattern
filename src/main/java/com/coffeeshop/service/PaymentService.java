package com.coffeeshop.service;

import com.coffeeshop.domain.model.Order;
import com.coffeeshop.domain.model.Payment;
import com.coffeeshop.domain.patterns.adapter.PaymentGateway;
import com.coffeeshop.domain.patterns.adapter.PaymentResult;
import com.coffeeshop.infrastructure.InMemoryRepository;

public class PaymentService {
    private final InMemoryRepository repository;

    public PaymentService(InMemoryRepository repository) {
        this.repository = repository;
    }

    public PaymentResult pay(Order order, PaymentGateway gateway) {
        PaymentResult result = gateway.processPayment(order.getTotalAmount());
        if (result.isSuccess()) {
            order.getState().pay(order);
            Payment payment = new Payment(repository.nextPaymentId(), order.getId(), gateway.getGatewayName(),
                    order.getTotalAmount(), result.getTransactionCode(), "SUCCESS");
            order.setPayment(payment);
            repository.savePayment(payment);
        }
        return result;
    }
}
