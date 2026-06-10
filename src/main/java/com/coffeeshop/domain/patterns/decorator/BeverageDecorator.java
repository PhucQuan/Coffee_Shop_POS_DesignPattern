package com.coffeeshop.domain.patterns.decorator;

public abstract class BeverageDecorator implements Beverage {
    protected final Beverage wrapped;

    protected BeverageDecorator(Beverage wrapped) {
        this.wrapped = wrapped;
    }
}
