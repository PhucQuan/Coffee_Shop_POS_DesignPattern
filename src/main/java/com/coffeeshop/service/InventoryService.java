package com.coffeeshop.service;

import com.coffeeshop.domain.model.InventoryItem;
import com.coffeeshop.domain.model.Order;
import com.coffeeshop.domain.model.OrderItem;
import com.coffeeshop.infrastructure.InMemoryRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InventoryService {
    private final InMemoryRepository repository;

    public InventoryService(InMemoryRepository repository) {
        this.repository = repository;
    }

    public List<InventoryItem> getInventory() {
        return repository.getInventory();
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
        switch (item.getBeverageId()) {
            case 1 -> add(requirements, "Coffee beans", 18 * quantity);
            case 2 -> {
                add(requirements, "Coffee beans", 15 * quantity);
                add(requirements, "Fresh milk", 80 * quantity);
            }
            case 3 -> {
                add(requirements, "Tea leaves", 8 * quantity);
                add(requirements, "Peach syrup", 40 * quantity);
            }
            case 4 -> {
                add(requirements, "Tea leaves", 10 * quantity);
                add(requirements, "Fresh milk", 100 * quantity);
            }
            case 5 -> {
                add(requirements, "Matcha powder", 12 * quantity);
                add(requirements, "Fresh milk", 120 * quantity);
            }
            case 6 -> add(requirements, "Mango", 150 * quantity);
            case 7 -> add(requirements, "Coffee beans", 18 * quantity);
            case 8 -> add(requirements, "Coffee beans", 16 * quantity);
            case 9, 10 -> {
                add(requirements, "Coffee beans", 18 * quantity);
                add(requirements, "Fresh milk", 120 * quantity);
            }
            case 11 -> add(requirements, "Coffee beans", 22 * quantity);
            case 12 -> {
                add(requirements, "Tea leaves", 8 * quantity);
                add(requirements, "Peach syrup", 20 * quantity);
            }
            case 13 -> add(requirements, "Tea leaves", 8 * quantity);
            case 14 -> {
                add(requirements, "Matcha powder", 15 * quantity);
                add(requirements, "Fresh milk", 100 * quantity);
            }
            case 15 -> add(requirements, "Mango", 120 * quantity);
            case 16 -> add(requirements, "Fresh milk", 150 * quantity);
            default -> add(requirements, "Coffee beans", 10 * quantity);
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

    private void add(Map<String, Double> requirements, String name, double quantity) {
        requirements.merge(name, quantity, Double::sum);
    }
}
