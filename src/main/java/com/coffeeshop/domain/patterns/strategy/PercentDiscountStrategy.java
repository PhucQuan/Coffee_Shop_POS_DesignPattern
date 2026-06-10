package com.coffeeshop.domain.patterns.strategy;

import com.coffeeshop.domain.model.Order;

public class PercentDiscountStrategy implements DiscountStrategy {
    private final double percent;

    public PercentDiscountStrategy(double percent) {
        this.percent = percent;
    }

    public double calculateDiscount(Order order) {
        return order.getSubtotal() * percent / 100.0;
    }

    public String getName() { return "PERCENT_" + String.format("%.0f", percent); }
}
