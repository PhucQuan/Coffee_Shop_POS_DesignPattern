package com.coffeeshop.domain.patterns.state;

import com.coffeeshop.domain.model.Order;

public class ReadyState extends AbstractOrderState {
    public String getName() { return "READY"; }
    public void pay(Order order) { order.setState(new PaidState()); }
    public void cancel(Order order) { order.setState(new CancelledState()); }
}
