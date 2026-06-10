package com.coffeeshop.domain.patterns.observer;

import com.coffeeshop.domain.model.Order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderEventPublisher {
    private final List<OrderObserver> observers = new ArrayList<>();
    private final List<String> eventLog = new ArrayList<>();

    public void subscribe(OrderObserver observer) { observers.add(observer); }
    public void unsubscribe(OrderObserver observer) { observers.remove(observer); }

    public void notifyObservers(Order order, String newStatus) {
        eventLog.add("Order #" + order.getId() + " -> " + newStatus);
        for (OrderObserver observer : observers) {
            observer.onOrderStatusChanged(order, newStatus);
        }
    }

    public List<String> getEventLog() {
        return Collections.unmodifiableList(eventLog);
    }
}
