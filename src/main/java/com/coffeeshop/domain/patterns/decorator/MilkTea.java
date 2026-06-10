package com.coffeeshop.domain.patterns.decorator;

public class MilkTea implements Beverage {
    private final String name;
    private final double basePrice;

    public MilkTea(String name, double basePrice) {
        this.name = name;
        this.basePrice = basePrice;
    }

    @Override
    public String getDescription() { return name; }

    @Override
    public double getPrice() { return basePrice; }
}
