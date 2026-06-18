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
        InventoryItem item = getInventoryItemById(id);
        item.setQuantity(item.getQuantity() + amount);
        // Note: For full SQLite support, we should call repository.saveInventoryItem(item) here.
        // Currently InMemoryRepository mutates the object directly.
    }

    public void deductForOrder(Order order) {
        if (order.isInventoryDeducted()) {
            return;
        }
        Map<String, Double> requirements = calculateRequirements(order);
        validateAvailable(requirements);
        requirements.forEach((name, quantity) -> getItem(name).setQuantity(getItem(name).getQuantity() - quantity));
        order.setInventoryDeducted(true);
    }

    public void restockForOrder(Order order) {
        if (!order.isInventoryDeducted()) {
            return;
        }
        Map<String, Double> requirements = calculateRequirements(order);
        requirements.forEach((name, quantity) -> getItem(name).setQuantity(getItem(name).getQuantity() + quantity));
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
        if (description.contains("Tran chau")) add(requirements, "Pearl", 30 * quantity);
        if (description.contains("Extra shot")) add(requirements, "Coffee beans", 8 * quantity);
        if (description.contains("Size L")) add(requirements, "Cup L", quantity);
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
}
