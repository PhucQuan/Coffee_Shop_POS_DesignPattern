package com.coffeeshop.service;

import com.coffeeshop.domain.model.Order;
import com.coffeeshop.domain.model.OrderItem;
import com.coffeeshop.domain.patterns.decorator.Beverage;
import com.coffeeshop.domain.patterns.observer.OrderEventPublisher;
import com.coffeeshop.domain.patterns.strategy.DiscountStrategy;
import com.coffeeshop.domain.patterns.strategy.NoDiscountStrategy;
import com.coffeeshop.infrastructure.InMemoryRepository;

public class OrderService {
    private final InMemoryRepository repository;
    private final OrderEventPublisher publisher;
    private final InventoryService inventoryService;
    private DiscountStrategy discountStrategy = new NoDiscountStrategy();

    public OrderService(InMemoryRepository repository, OrderEventPublisher publisher) {
        this(repository, publisher, new InventoryService(repository));
    }

    public OrderService(InMemoryRepository repository, OrderEventPublisher publisher, InventoryService inventoryService) {
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
        OrderItem item = new OrderItem(repository.nextOrderItemId(), order.getId(), beverageId, beverage, quantity, note);
        order.getState().addItem(order, item);
        recalculate(order);
        return item;
    }

    public void removeItem(Order order, int itemId) {
        order.getState().removeItem(order, itemId);
        recalculate(order);
    }

    public void setDiscountStrategy(DiscountStrategy discountStrategy) {
        this.discountStrategy = discountStrategy;
    }

    public void recalculate(Order order) {
        double discount = discountStrategy.calculateDiscount(order);
        double finalTotal = Math.max(0, order.getSubtotal() - discount);
        order.setDiscountType(discountStrategy.getName());
        order.setDiscountAmount(discount);
        order.setTotalAmount(finalTotal);
    }

    public void sendToKitchen(Order order) {
        inventoryService.deductForOrder(order);
        order.getState().sendToKitchen(order);
        publisher.notifyObservers(order, order.getStatus());
    }

    public void startPreparing(Order order) {
        inventoryService.deductForOrder(order);
        order.getState().startPreparing(order);
        publisher.notifyObservers(order, order.getStatus());
    }

    public void markReady(Order order) {
        order.getState().markReady(order);
        publisher.notifyObservers(order, order.getStatus());
    }

    public void cancel(Order order) {
        order.getState().cancel(order);
        inventoryService.restockForOrder(order);
        publisher.notifyObservers(order, order.getStatus());
    }
}
