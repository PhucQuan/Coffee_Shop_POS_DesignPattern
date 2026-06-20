package com.coffeeshop.domain.patterns.strategy;

import com.coffeeshop.domain.model.Order;

public class VipDiscountStrategy implements DiscountStrategy {
    private final double fixedAmount;

    public VipDiscountStrategy(double fixedAmount) {
        this.fixedAmount = fixedAmount;
    }

    public double calculateDiscount(Order order) {
        return Math.min(fixedAmount, order.getSubtotal());
    }

    public String getName() { return "VIP_FIXED_" + String.format("%.0f", fixedAmount); }
}
