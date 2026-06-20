package com.coffeeshop.domain.patterns.decorator;

public class PricedToppingDecorator extends BeverageDecorator {
    private final String toppingName;
    private final double extraPrice;

    public PricedToppingDecorator(Beverage wrapped, String toppingName, double extraPrice) {
        super(wrapped);
        this.toppingName = toppingName;
        this.extraPrice = extraPrice;
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + " + " + toppingName;
    }

    @Override
    public double getPrice() {
        return wrapped.getPrice() + extraPrice;
    }
}
