package com.coffeeshop.domain.patterns.decorator;

public class ExtraShotDecorator extends BeverageDecorator {
    public ExtraShotDecorator(Beverage wrapped) { super(wrapped); }
    public String getDescription() { return wrapped.getDescription() + " + Extra shot"; }
    public double getPrice() { return wrapped.getPrice() + 8000; }
}
