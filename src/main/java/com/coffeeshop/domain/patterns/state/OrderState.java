package com.coffeeshop.domain.patterns.state;

import com.coffeeshop.domain.model.Order;
import com.coffeeshop.domain.model.OrderItem;

public interface OrderState {
    String getName();
    void addItem(Order order, OrderItem item);
    void removeItem(Order order, int itemId);
    void sendToKitchen(Order order);
    void startPreparing(Order order);
    void markReady(Order order);
    void pay(Order order);
    void cancel(Order order);
}
