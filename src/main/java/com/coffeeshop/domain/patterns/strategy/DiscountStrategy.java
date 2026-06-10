package com.coffeeshop.domain.patterns.strategy;

import com.coffeeshop.domain.model.Order;

public interface DiscountStrategy {
    double calculateDiscount(Order order);
    String getName();
}
