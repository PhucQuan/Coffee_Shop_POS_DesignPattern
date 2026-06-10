package com.coffeeshop.domain.patterns.observer;

import com.coffeeshop.domain.model.Order;

public interface OrderObserver {
    void onOrderStatusChanged(Order order, String newStatus);
}
