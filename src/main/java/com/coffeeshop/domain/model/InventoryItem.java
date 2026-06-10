package com.coffeeshop.domain.model;

public class InventoryItem {
    private final int id;
    private final String name;
    private final String unit;
    private double quantity;
    private final double reorderLevel;

    public InventoryItem(int id, String name, String unit, double quantity, double reorderLevel) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.quantity = quantity;
        this.reorderLevel = reorderLevel;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getUnit() { return unit; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public double getReorderLevel() { return reorderLevel; }
    public boolean isLowStock() { return quantity <= reorderLevel; }

    @Override
    public String toString() {
        return name + ": " + String.format("%,.1f", quantity) + " " + unit + (isLowStock() ? " (LOW)" : "");
    }
}
