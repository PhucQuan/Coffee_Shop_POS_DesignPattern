package com.coffeeshop.domain.patterns.factory;

import com.coffeeshop.domain.patterns.decorator.Beverage;

public abstract class BeverageFactory {
    public abstract Beverage createBeverage(String name, double basePrice);
}
