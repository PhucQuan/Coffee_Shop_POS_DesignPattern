package com.coffeeshop.domain.model;

public class Topping {
    private final int id;
    private String name;
    private double extraPrice;
    private boolean active;

    public Topping(int id, String name, double extraPrice, boolean active) {
        this.id = id;
        this.name = name;
        this.extraPrice = extraPrice;
        this.active = active;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getExtraPrice() { return extraPrice; }
    public void setExtraPrice(double extraPrice) { this.extraPrice = extraPrice; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return name + " (+" + String.format("%,.0f", extraPrice) + "d)" + (active ? "" : " (inactive)");
    }
}
