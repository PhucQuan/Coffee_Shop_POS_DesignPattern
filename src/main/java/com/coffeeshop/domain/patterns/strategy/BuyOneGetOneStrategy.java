package com.coffeeshop.domain.patterns.strategy;

import com.coffeeshop.domain.model.Order;

public class BuyOneGetOneStrategy implements DiscountStrategy {
    public double calculateDiscount(Order order) {
        if (order.getItems().size() < 2) {
            return 0;
        }
        return order.getItems().stream()
                .mapToDouble(item -> item.getBeverage().getPrice())
                .min()
                .orElse(0);
    }

    public String getName() { return "BUY_ONE_GET_ONE"; }
}
