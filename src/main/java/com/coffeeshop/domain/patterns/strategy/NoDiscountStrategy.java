package com.coffeeshop.domain.patterns.strategy;

import com.coffeeshop.domain.model.Order;

public class NoDiscountStrategy implements DiscountStrategy {
    public double calculateDiscount(Order order) { return 0; }
    public String getName() { return "NONE"; }
}
