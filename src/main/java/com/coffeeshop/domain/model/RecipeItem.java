package com.coffeeshop.domain.model;

public class RecipeItem {
    private int beverageId;
    private int inventoryItemId;
    private double quantityRequired;

    public RecipeItem(int beverageId, int inventoryItemId, double quantityRequired) {
        this.beverageId = beverageId;
        this.inventoryItemId = inventoryItemId;
        this.quantityRequired = quantityRequired;
    }

    public int getBeverageId() { return beverageId; }
    public void setBeverageId(int beverageId) { this.beverageId = beverageId; }

    public int getInventoryItemId() { return inventoryItemId; }
    public void setInventoryItemId(int inventoryItemId) { this.inventoryItemId = inventoryItemId; }

    public double getQuantityRequired() { return quantityRequired; }
    public void setQuantityRequired(double quantityRequired) { this.quantityRequired = quantityRequired; }
}
