package com.coffeeshop.domain.patterns.state;

import com.coffeeshop.domain.model.Order;

public class PreparingState extends AbstractOrderState {
    public String getName() { return "PREPARING"; }
    public void markReady(Order order) { order.setState(new ReadyState()); }
    public void cancel(Order order) { order.setState(new CancelledState()); }
}
