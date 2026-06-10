package com.coffeeshop.domain.patterns.factory;

import com.coffeeshop.domain.patterns.decorator.Beverage;
import com.coffeeshop.domain.patterns.decorator.MilkTea;

public class TeaFactory extends BeverageFactory {
    public Beverage createBeverage(String name, double basePrice) {
        return new MilkTea(name, basePrice);
    }
}
