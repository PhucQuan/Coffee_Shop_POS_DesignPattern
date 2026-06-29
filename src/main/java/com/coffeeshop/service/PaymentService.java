package com.coffeeshop.service;

import com.coffeeshop.domain.model.Order;
import com.coffeeshop.domain.model.Payment;
import com.coffeeshop.domain.patterns.adapter.PaymentGateway;
import com.coffeeshop.domain.patterns.adapter.PaymentResult;
import com.coffeeshop.domain.patterns.observer.OrderEventPublisher;
import com.coffeeshop.infrastructure.Repository;

public class PaymentService {
    private final Repository repository;
    private final InventoryService inventoryService;
    private final OrderEventPublisher publisher;

    public PaymentService(Repository repository) {
        this(repository, new InventoryService(repository), null);
    }

    public PaymentService(Repository repository, InventoryService inventoryService, OrderEventPublisher publisher) {
        this.repository = repository;
        this.inventoryService = inventoryService;
        this.publisher = publisher;
    }

    public PaymentResult pay(Order order, PaymentGateway gateway) {
        if (order.getPayment() != null && "SUCCESS".equals(order.getPayment().getStatus())) {
            throw new IllegalStateException("Order has already been paid.");
        }
        PaymentResult result = gateway.processPayment(order.getTotalAmount());
        if (result.isSuccess()) {
            Payment payment = new Payment(repository.nextPaymentId(), order.getId(), gateway.getGatewayName(),
                    order.getTotalAmount(), result.getTransactionCode(), "SUCCESS");
            order.setPayment(payment);
            repository.savePayment(payment);
            if ("PENDING".equals(order.getStatus())) {
                inventoryService.deductForOrder(order);
                order.getState().sendToKitchen(order);
            } else {
                order.getState().pay(order);
            }
            repository.saveOrder(order);
            if (publisher != null) {
                publisher.notifyObservers(order, order.getStatus());
            }
        }
        return result;
    }
}
