package com.coffeeshop.domain.patterns.decorator;

public class PearlDecorator extends BeverageDecorator {
    public PearlDecorator(Beverage wrapped) { super(wrapped); }
    public String getDescription() { return wrapped.getDescription() + " + Tran chau"; }
    public double getPrice() { return wrapped.getPrice() + 10000; }
}
