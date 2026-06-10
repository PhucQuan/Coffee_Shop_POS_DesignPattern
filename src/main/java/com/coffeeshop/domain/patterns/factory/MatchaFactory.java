package com.coffeeshop.domain.patterns.factory;

import com.coffeeshop.domain.patterns.decorator.Beverage;
import com.coffeeshop.domain.patterns.decorator.Matcha;

public class MatchaFactory extends BeverageFactory {
    public Beverage createBeverage(String name, double basePrice) {
        return new Matcha(name, basePrice);
    }
}
