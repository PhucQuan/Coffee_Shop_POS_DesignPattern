package com.coffeeshop.domain.patterns.state;

import com.coffeeshop.domain.model.Order;
import com.coffeeshop.domain.model.OrderItem;

abstract class AbstractOrderState implements OrderState {
    protected void invalid(String action) {
        throw new InvalidStateTransitionException("Cannot " + action + " when order is " + getName());
    }

    public void addItem(Order order, OrderItem item) { invalid("add item"); }
    public void removeItem(Order order, int itemId) { invalid("remove item"); }
    public void sendToKitchen(Order order) { invalid("send to kitchen"); }
    public void startPreparing(Order order) { invalid("start preparing"); }
    public void markReady(Order order) { invalid("mark ready"); }
    public void pay(Order order) { invalid("pay"); }
    public void cancel(Order order) { invalid("cancel"); }
}
