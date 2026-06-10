package com.coffeeshop.domain.model;

import com.coffeeshop.domain.patterns.decorator.Beverage;

public class OrderItem {
    private final int id;
    private final int orderId;
    private final int beverageId;
    private final Beverage beverage;
    private int quantity;
    private String note;
    private double itemPrice;

    public OrderItem(int id, int orderId, int beverageId, Beverage beverage, int quantity, String note) {
        this.id = id;
        this.orderId = orderId;
        this.beverageId = beverageId;
        this.beverage = beverage;
        this.quantity = quantity;
        this.note = note;
        recalculate();
    }

    public void recalculate() {
        this.itemPrice = beverage.getPrice() * quantity;
    }

    public int getId() { return id; }
    public int getOrderId() { return orderId; }
    public int getBeverageId() { return beverageId; }
    public Beverage getBeverage() { return beverage; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; recalculate(); }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public double getItemPrice() { return itemPrice; }

    @Override
    public String toString() {
        return quantity + " x " + beverage.getDescription() + " = " + String.format("%,.0f", itemPrice) + "d";
    }
}
