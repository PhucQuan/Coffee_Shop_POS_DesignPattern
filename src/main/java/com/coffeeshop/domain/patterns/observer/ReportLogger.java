package com.coffeeshop.domain.patterns.observer;

import com.coffeeshop.domain.model.Order;

import java.util.ArrayList;
import java.util.List;

public class ReportLogger implements OrderObserver {
    private final List<String> logs = new ArrayList<>();

    public void onOrderStatusChanged(Order order, String newStatus) {
        logs.add("Order #" + order.getId() + " changed to " + newStatus);
    }

    public List<String> getLogs() { return logs; }
}
