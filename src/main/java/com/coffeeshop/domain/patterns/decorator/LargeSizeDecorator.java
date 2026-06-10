package com.coffeeshop.domain.patterns.decorator;

public class LargeSizeDecorator extends BeverageDecorator {
    public LargeSizeDecorator(Beverage wrapped) { super(wrapped); }
    public String getDescription() { return wrapped.getDescription() + " + Size L"; }
    public double getPrice() { return wrapped.getPrice() + 7000; }
}
