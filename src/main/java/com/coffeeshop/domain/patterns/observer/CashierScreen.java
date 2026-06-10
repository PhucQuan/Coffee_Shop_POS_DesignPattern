package com.coffeeshop.domain.patterns.observer;

import com.coffeeshop.domain.model.Order;

public class CashierScreen implements OrderObserver {
    private String lastMessage = "";

    public void onOrderStatusChanged(Order order, String newStatus) {
        if ("READY".equals(newStatus)) {
            lastMessage = "Cashier: Order #" + order.getId() + " is ready for pickup.";
            System.out.println(lastMessage);
        }
    }

    public String getLastMessage() { return lastMessage; }
}
