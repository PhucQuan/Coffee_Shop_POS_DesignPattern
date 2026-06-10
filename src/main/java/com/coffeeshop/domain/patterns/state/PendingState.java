package com.coffeeshop.domain.patterns.state;

import com.coffeeshop.domain.model.Order;
import com.coffeeshop.domain.model.OrderItem;

public class PendingState extends AbstractOrderState {
    public String getName() { return "PENDING"; }
    public void addItem(Order order, OrderItem item) { order.addRawItem(item); }
    public void removeItem(Order order, int itemId) { order.removeRawItem(itemId); }
    public void sendToKitchen(Order order) { order.setState(new PreparingState()); }
    public void startPreparing(Order order) { order.setState(new PreparingState()); }
    public void cancel(Order order) { order.setState(new CancelledState()); }
}
