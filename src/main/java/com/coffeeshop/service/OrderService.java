package com.coffeeshop.service;

import com.coffeeshop.domain.model.Order;
import com.coffeeshop.domain.model.OrderItem;
import com.coffeeshop.domain.patterns.decorator.Beverage;
import com.coffeeshop.domain.patterns.observer.OrderEventPublisher;
import com.coffeeshop.domain.patterns.state.InvalidStateTransitionException;
import com.coffeeshop.domain.patterns.strategy.DiscountStrategy;
import com.coffeeshop.domain.patterns.strategy.DiscountStrategyResolver;
import com.coffeeshop.infrastructure.Repository;

import java.util.Objects;

public class OrderService {
    private final Repository repository;
    private final OrderEventPublisher publisher;
    private final InventoryService inventoryService;

    public OrderService(Repository repository, OrderEventPublisher publisher) {
        this(repository, publisher, new InventoryService(repository));
    }

    public OrderService(Repository repository, OrderEventPublisher publisher, InventoryService inventoryService) {
        this.repository = repository;
        this.publisher = publisher;
        this.inventoryService = inventoryService;
    }

    public Order createOrder() {
        Order order = new Order(repository.nextOrderId());
        repository.saveOrder(order);
        return order;
    }

    public OrderItem addItem(Order order, int beverageId, Beverage beverage, int quantity, String note) {
        ensurePending(order, "add item");
        for (OrderItem existing : order.getItems()) {
            if (existing.getBeverage().getDescription().equals(beverage.getDescription())
                    && Objects.equals(existing.getNote(), note == null ? "" : note)) {
                existing.setQuantity(existing.getQuantity() + quantity);
                recalculate(order);
                return existing;
            }
        }
        OrderItem item = new OrderItem(repository.nextOrderItemId(), order.getId(), beverageId, beverage, quantity, note);
        order.getState().addItem(order, item);
        recalculate(order);
        return item;
    }

    public void removeItem(Order order, int itemId) {
        order.getState().removeItem(order, itemId);
        recalculate(order);
    }

    public void updateItemQuantity(Order order, int itemId, int quantity) {
        ensurePending(order, "update item quantity");
        if (quantity <= 0) {
            removeItem(order, itemId);
            return;
        }
        OrderItem item = order.getItems().stream()
                .filter(orderItem -> orderItem.getId() == itemId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Order item not found: " + itemId));
        item.setQuantity(quantity);
        recalculate(order);
    }

    public void setDiscountStrategy(Order order, DiscountStrategy discountStrategy) {
        order.setDiscountType(discountStrategy == null ? "NONE" : discountStrategy.getName());
    }

    public void recalculate(Order order) {
        DiscountStrategy discountStrategy = DiscountStrategyResolver.fromName(order.getDiscountType());
        double discount = discountStrategy.calculateDiscount(order);
        double finalTotal = Math.max(0, order.getSubtotal() - discount);
        order.setDiscountType(discountStrategy.getName());
        order.setDiscountAmount(discount);
        order.setTotalAmount(finalTotal);
        repository.saveOrder(order);
    }

    public void sendToKitchen(Order order) {
        inventoryService.deductForOrder(order);
        order.getState().sendToKitchen(order);
        repository.saveOrder(order);
        publisher.notifyObservers(order, order.getStatus());
    }

    public void startPreparing(Order order) {
        inventoryService.deductForOrder(order);
        order.getState().startPreparing(order);
        repository.saveOrder(order);
        publisher.notifyObservers(order, order.getStatus());
    }

    public void markReady(Order order) {
        order.getState().markReady(order);
        repository.saveOrder(order);
        publisher.notifyObservers(order, order.getStatus());
    }

    public void cancel(Order order) {
        order.getState().cancel(order);
        inventoryService.restockForOrder(order);
        repository.saveOrder(order);
        publisher.notifyObservers(order, order.getStatus());
    }

    private void ensurePending(Order order, String action) {
        if (!"PENDING".equals(order.getStatus())) {
            throw new InvalidStateTransitionException("Cannot " + action + " when order is " + order.getStatus() + ".");
        }
    }
}
