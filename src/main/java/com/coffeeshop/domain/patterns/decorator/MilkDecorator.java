package com.coffeeshop.domain.patterns.decorator;

public class MilkDecorator extends BeverageDecorator {
    public MilkDecorator(Beverage wrapped) { super(wrapped); }
    public String getDescription() { return wrapped.getDescription() + " + Sua"; }
    public double getPrice() { return wrapped.getPrice() + 5000; }
}
