package com.coffeeshop.service;

import com.coffeeshop.domain.patterns.decorator.Beverage;
import com.coffeeshop.domain.patterns.decorator.PricedToppingDecorator;
import com.coffeeshop.domain.patterns.factory.BeverageFactory;
import com.coffeeshop.domain.patterns.factory.CoffeeFactory;
import com.coffeeshop.domain.patterns.factory.MatchaFactory;
import com.coffeeshop.domain.patterns.factory.SmoothieFactory;
import com.coffeeshop.domain.patterns.factory.TeaFactory;
import com.coffeeshop.domain.model.Topping;
import com.coffeeshop.infrastructure.MenuItemRecord;
import com.coffeeshop.infrastructure.Repository;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class MenuService {
    private final Repository repository;

    public MenuService(Repository repository) {
        this.repository = repository;
    }

    public List<MenuItemRecord> getActiveMenu() {
        return repository.getMenu().stream().filter(MenuItemRecord::isActive).toList();
    }

    public List<MenuItemRecord> getAllMenu() {
        return repository.getMenu();
    }

    public List<Topping> getActiveToppings() {
        return repository.getToppings().stream().filter(Topping::isActive).toList();
    }

    public List<Topping> getAllToppings() {
        return repository.getToppings();
    }

    public Beverage createBeverage(MenuItemRecord item) {
        BeverageFactory factory = switch (item.getCategory()) {
            case "COFFEE" -> new CoffeeFactory();
            case "TEA" -> new TeaFactory();
            case "MATCHA" -> new MatchaFactory();
            case "SMOOTHIE" -> new SmoothieFactory();
            default -> throw new IllegalArgumentException("Unsupported beverage category: " + item.getCategory());
        };
        return factory.createBeverage(item.getName(), item.getBasePrice());
    }

    public Beverage applyTopping(Beverage beverage, String toppingName) {
        Topping topping = repository.getToppings().stream()
                .filter(item -> item.getName().equalsIgnoreCase(toppingName == null ? "" : toppingName.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown topping: " + toppingName));
        return new PricedToppingDecorator(beverage, topping.getName(), topping.getExtraPrice());
    }

    public MenuItemRecord addBeverage(String name, double basePrice, String category) {
        validateName(name);
        validatePrice(basePrice);
        String normalizedCategory = normalizeCategory(category);
        ensureUniqueMenuName(name, -1);
        MenuItemRecord item = new MenuItemRecord(repository.nextMenuId(), name.trim(), basePrice, normalizedCategory, true);
        repository.saveMenuItem(item);
        return item;
    }

    public void updateBeverage(MenuItemRecord item, String name, double basePrice, String category, boolean active) {
        if (item == null) {
            throw new IllegalArgumentException("Please select a beverage to update.");
        }
        validateName(name);
        validatePrice(basePrice);
        String normalizedCategory = normalizeCategory(category);
        ensureUniqueMenuName(name, item.getId());
        item.setName(name.trim());
        item.setBasePrice(basePrice);
        item.setCategory(normalizedCategory);
        item.setActive(active);
        repository.saveMenuItem(item);
    }

    public void disableBeverage(MenuItemRecord item) {
        if (item == null) {
            throw new IllegalArgumentException("Please select a beverage to disable.");
        }
        item.setActive(false);
        repository.saveMenuItem(item);
    }

    public Topping addTopping(String name, double extraPrice) {
        validateName(name);
        validatePrice(extraPrice);
        ensureUniqueToppingName(name, -1);
        Topping topping = new Topping(repository.nextToppingId(), name.trim(), extraPrice, true);
        repository.saveTopping(topping);
        return topping;
    }

    public void updateTopping(Topping topping, String name, double extraPrice, boolean active) {
        if (topping == null) {
            throw new IllegalArgumentException("Please select a topping to update.");
        }
        validateName(name);
        validatePrice(extraPrice);
        ensureUniqueToppingName(name, topping.getId());
        topping.setName(name.trim());
        topping.setExtraPrice(extraPrice);
        topping.setActive(active);
        repository.saveTopping(topping);
    }

    public void disableTopping(Topping topping) {
        if (topping == null) {
            throw new IllegalArgumentException("Please select a topping to disable.");
        }
        topping.setActive(false);
        repository.saveTopping(topping);
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name must not be empty.");
        }
    }

    private void validatePrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price must be greater than or equal to 0.");
        }
    }

    private String normalizeCategory(String category) {
        String value = category == null ? "" : category.trim().toUpperCase(Locale.ROOT);
        return switch (value) {
            case "COFFEE", "TEA", "MATCHA", "SMOOTHIE" -> value;
            default -> throw new IllegalArgumentException("Category must be COFFEE, TEA, MATCHA, or SMOOTHIE.");
        };
    }

    private void ensureUniqueMenuName(String name, int currentId) {
        Optional<MenuItemRecord> duplicated = repository.getMenu().stream()
                .filter(item -> item.getId() != currentId)
                .filter(item -> item.getName().equalsIgnoreCase(name.trim()))
                .findFirst();
        if (duplicated.isPresent()) {
            throw new IllegalArgumentException("Beverage name already exists.");
        }
    }

    private void ensureUniqueToppingName(String name, int currentId) {
        Optional<Topping> duplicated = repository.getToppings().stream()
                .filter(topping -> topping.getId() != currentId)
                .filter(topping -> topping.getName().equalsIgnoreCase(name.trim()))
                .findFirst();
        if (duplicated.isPresent()) {
            throw new IllegalArgumentException("Topping name already exists.");
        }
    }
}
