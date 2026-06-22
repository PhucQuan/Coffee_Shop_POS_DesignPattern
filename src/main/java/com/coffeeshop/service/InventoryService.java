package com.coffeeshop.service;

import com.coffeeshop.domain.model.InventoryItem;
import com.coffeeshop.domain.model.Order;
import com.coffeeshop.domain.model.OrderItem;
import com.coffeeshop.domain.model.RecipeItem;
import com.coffeeshop.infrastructure.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InventoryService {
    private final Repository repository;

    public InventoryService(Repository repository) {
        this.repository = repository;
    }

    public List<InventoryItem> getInventory() {
        return repository.getInventory();
    }

    public void restockItem(int id, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Restock amount must be greater than 0.");
        }
        InventoryItem item = getInventoryItemById(id);
        repository.adjustInventory(item.getId(), amount, null, "MANUAL_RESTOCK");
    }

    public void deductForOrder(Order order) {
        if (order.isInventoryDeducted()) {
            return;
        }
        Map<String, Double> requirements = calculateRequirements(order);
        validateAvailable(requirements);
        requirements.forEach((name, quantity) -> {
            InventoryItem item = getItem(name);
            repository.adjustInventory(item.getId(), -quantity, order.getId(), "ORDER_DEDUCTION");
        });
        order.setInventoryDeducted(true);
    }

    public void restockForOrder(Order order) {
        if (!order.isInventoryDeducted()) {
            return;
        }
        Map<String, Double> requirements = calculateRequirements(order);
        requirements.forEach((name, quantity) -> {
            InventoryItem item = getItem(name);
            repository.adjustInventory(item.getId(), quantity, order.getId(), "ORDER_RESTOCK");
        });
        order.setInventoryDeducted(false);
    }

    public Map<String, Double> calculateRequirements(Order order) {
        Map<String, Double> requirements = new LinkedHashMap<>();
        for (OrderItem item : order.getItems()) {
            addRequirements(requirements, item);
        }
        return requirements;
    }

    private void addRequirements(Map<String, Double> requirements, OrderItem item) {
        double quantity = item.getQuantity();
        
        List<RecipeItem> recipeItems = repository.getRecipeItems(item.getBeverageId());
        if (recipeItems.isEmpty()) {
            add(requirements, "Coffee beans", 10 * quantity);
        } else {
            for (RecipeItem recipeItem : recipeItems) {
                InventoryItem invItem = getInventoryItemById(recipeItem.getInventoryItemId());
                add(requirements, invItem.getName(), recipeItem.getQuantityRequired() * quantity);
            }
        }

        String description = item.getBeverage().getDescription();
        if (hasAny(description, "Tran chau", "Trân châu trắng")) add(requirements, "Pearl", 30 * quantity);
        if (description.contains("Pudding")) add(requirements, "Pudding", 35 * quantity);
        if (description.contains("Kem cheese")) add(requirements, "Cream cheese", 25 * quantity);
        if (description.contains("Extra shot")) add(requirements, "Coffee beans", 8 * quantity);
        if (description.contains("Size L")) add(requirements, "Cup L", quantity);
        if (description.contains("Kem muối")) add(requirements, "Salted cream", 25 * quantity);
        if (description.contains("Thach cafe")) add(requirements, "Coffee jelly", 35 * quantity);
        if (description.contains("Kem vani")) add(requirements, "Vanilla cream", 25 * quantity);
        if (hasAny(description, "Duong den", "Trân châu đường đen")) add(requirements, "Brown sugar syrup", 20 * quantity);
        if (description.contains("Trân châu đường đen")) add(requirements, "Pearl", 30 * quantity);
    }

    private void validateAvailable(Map<String, Double> requirements) {
        for (Map.Entry<String, Double> requirement : requirements.entrySet()) {
            InventoryItem item = getItem(requirement.getKey());
            if (item.getQuantity() < requirement.getValue()) {
                throw new InventoryException("Not enough inventory: " + item.getName()
                        + ". Required " + requirement.getValue() + " " + item.getUnit()
                        + ", available " + item.getQuantity() + " " + item.getUnit() + ".");
            }
        }
    }

    private InventoryItem getItem(String name) {
        return repository.getInventory().stream()
                .filter(item -> item.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new InventoryException("Missing inventory item: " + name));
    }

    private InventoryItem getInventoryItemById(int id) {
        return repository.getInventory().stream()
                .filter(item -> item.getId() == id)
                .findFirst()
                .orElseThrow(() -> new InventoryException("Missing inventory item id: " + id));
    }

    private void add(Map<String, Double> requirements, String name, double quantity) {
        requirements.merge(name, quantity, Double::sum);
    }

    private boolean hasAny(String value, String... tokens) {
        for (String token : tokens) {
            if (value.contains(token)) return true;
        }
        return false;
    }
}
