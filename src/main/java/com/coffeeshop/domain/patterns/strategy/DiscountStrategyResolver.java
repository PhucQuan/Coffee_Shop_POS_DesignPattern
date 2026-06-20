package com.coffeeshop.domain.patterns.strategy;

import java.util.Locale;

public final class DiscountStrategyResolver {
    private DiscountStrategyResolver() {}

    public static DiscountStrategy fromName(String name) {
        if (name == null || name.isBlank() || "NONE".equalsIgnoreCase(name)) {
            return new NoDiscountStrategy();
        }

        String normalized = name.trim().toUpperCase(Locale.ROOT);
        if (normalized.startsWith("PERCENT_")) {
            double percent = parseTrailingNumber(normalized.substring("PERCENT_".length()));
            return new PercentDiscountStrategy(percent);
        }
        if (normalized.startsWith("VIP_FIXED_")) {
            double fixedAmount = parseTrailingNumber(normalized.substring("VIP_FIXED_".length()));
            return new VipDiscountStrategy(fixedAmount);
        }
        if ("BUY_ONE_GET_ONE".equals(normalized)) {
            return new BuyOneGetOneStrategy();
        }
        return new NoDiscountStrategy();
    }

    private static double parseTrailingNumber(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
