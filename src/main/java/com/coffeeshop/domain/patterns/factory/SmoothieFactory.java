package com.coffeeshop.domain.patterns.factory;

import com.coffeeshop.domain.patterns.decorator.BaseCoffee;
import com.coffeeshop.domain.patterns.decorator.Beverage;

public class SmoothieFactory extends BeverageFactory {
    public Beverage createBeverage(String name, double basePrice) {
        return new BaseCoffee(name, basePrice);
    }
}
