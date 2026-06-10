package com.coffeeshop.infrastructure;

import com.coffeeshop.domain.model.Order;
import com.coffeeshop.domain.model.Payment;
import com.coffeeshop.domain.model.Topping;
import com.coffeeshop.domain.model.User;
import com.coffeeshop.domain.model.InventoryItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryRepository {
    private final AtomicInteger orderId = new AtomicInteger(1);
    private final AtomicInteger orderItemId = new AtomicInteger(1);
    private final AtomicInteger paymentId = new AtomicInteger(1);
    private final AtomicInteger menuId = new AtomicInteger(17);
    private final AtomicInteger toppingId = new AtomicInteger(6);
    private final AtomicInteger userId = new AtomicInteger(4);
    private final List<User> users = new ArrayList<>();
    private final List<MenuItemRecord> menu = new ArrayList<>();
    private final List<Topping> toppings = new ArrayList<>();
    private final List<InventoryItem> inventory = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private final List<Payment> payments = new ArrayList<>();

    public InMemoryRepository() {
        seed();
    }

    private void seed() {
        users.add(new User(1, "admin", "123", "ADMIN", true));
        users.add(new User(2, "cashier01", "123", "CASHIER", true));
        users.add(new User(3, "kitchen01", "123", "KITCHEN", true));

        menu.add(new MenuItemRecord(1, "Ca phe sua", 30000, "COFFEE", true));
        menu.add(new MenuItemRecord(2, "Bac xiu", 32000, "COFFEE", true));
        menu.add(new MenuItemRecord(3, "Tra dao", 35000, "TEA", true));
        menu.add(new MenuItemRecord(4, "Tra sua", 38000, "TEA", true));
        menu.add(new MenuItemRecord(5, "Matcha latte", 42000, "MATCHA", true));
        menu.add(new MenuItemRecord(6, "Sinh to xoai", 45000, "SMOOTHIE", true));
        menu.add(new MenuItemRecord(7, "Espresso", 28000, "COFFEE", true));
        menu.add(new MenuItemRecord(8, "Americano", 30000, "COFFEE", true));
        menu.add(new MenuItemRecord(9, "Latte", 42000, "COFFEE", true));
        menu.add(new MenuItemRecord(10, "Cappuccino", 42000, "COFFEE", true));
        menu.add(new MenuItemRecord(11, "Cold brew", 45000, "COFFEE", true));
        menu.add(new MenuItemRecord(12, "Tra vai", 39000, "TEA", true));
        menu.add(new MenuItemRecord(13, "Tra tac mat ong", 34000, "TEA", true));
        menu.add(new MenuItemRecord(14, "Matcha da xay", 52000, "MATCHA", true));
        menu.add(new MenuItemRecord(15, "Sinh to dau", 48000, "SMOOTHIE", true));
        menu.add(new MenuItemRecord(16, "Cacao nong", 36000, "COFFEE", true));

        toppings.add(new Topping(1, "Tran chau", 10000, true));
        toppings.add(new Topping(2, "Pudding", 9000, true));
        toppings.add(new Topping(3, "Kem cheese", 12000, true));
        toppings.add(new Topping(4, "Extra shot", 8000, true));
        toppings.add(new Topping(5, "Size L", 7000, true));

        inventory.add(new InventoryItem(1, "Coffee beans", "g", 5000, 500));
        inventory.add(new InventoryItem(2, "Fresh milk", "ml", 10000, 1000));
        inventory.add(new InventoryItem(3, "Tea leaves", "g", 3000, 300));
        inventory.add(new InventoryItem(4, "Peach syrup", "ml", 2500, 300));
        inventory.add(new InventoryItem(5, "Matcha powder", "g", 2000, 200));
        inventory.add(new InventoryItem(6, "Mango", "g", 5000, 500));
        inventory.add(new InventoryItem(7, "Pearl", "g", 4000, 400));
        inventory.add(new InventoryItem(8, "Cup L", "pcs", 300, 30));
    }

    public Optional<User> findUser(String username, String password) {
        return users.stream()
                .filter(user -> user.isActive() && user.getUsername().equals(username) && user.getPasswordHash().equals(password))
                .findFirst();
    }

    public List<User> getUsers() { return users; }
    public List<MenuItemRecord> getMenu() { return menu; }
    public List<Topping> getToppings() { return toppings; }
    public List<InventoryItem> getInventory() { return inventory; }
    public List<Order> getOrders() { return orders; }
    public List<Payment> getPayments() { return payments; }
    public int nextOrderId() { return orderId.getAndIncrement(); }
    public int nextOrderItemId() { return orderItemId.getAndIncrement(); }
    public int nextPaymentId() { return paymentId.getAndIncrement(); }
    public int nextMenuId() { return menuId.getAndIncrement(); }
    public int nextToppingId() { return toppingId.getAndIncrement(); }
    public int nextUserId() { return userId.getAndIncrement(); }
    public void saveOrder(Order order) { if (!orders.contains(order)) orders.add(order); }
    public void savePayment(Payment payment) { payments.add(payment); }
}
