package com.coffeeshop.domain.patterns.observer;

import com.coffeeshop.domain.model.Order;

public class KitchenScreen implements OrderObserver {
    private String lastMessage = "";

    public void onOrderStatusChanged(Order order, String newStatus) {
        if ("PREPARING".equals(newStatus)) {
            lastMessage = "Kitchen: New order #" + order.getId() + " needs preparing.";
            System.out.println(lastMessage);
        }
    }

    public String getLastMessage() { return lastMessage; }
}
