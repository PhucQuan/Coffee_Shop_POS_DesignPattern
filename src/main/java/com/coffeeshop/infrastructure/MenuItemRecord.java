package com.coffeeshop.infrastructure;

public class MenuItemRecord {
    private final int id;
    private String name;
    private double basePrice;
    private String category;
    private boolean active;

    public MenuItemRecord(int id, String name, double basePrice, String category, boolean active) {
        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
        this.category = category;
        this.active = active;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return name + " [" + category + "] - " + String.format("%,.0f", basePrice) + "d" + (active ? "" : " (inactive)");
    }
}
