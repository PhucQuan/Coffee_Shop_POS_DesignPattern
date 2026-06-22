package com.coffeeshop.infrastructure;

import com.coffeeshop.domain.model.Order;
import com.coffeeshop.domain.model.Payment;
import com.coffeeshop.domain.model.Topping;
import com.coffeeshop.domain.model.User;
import com.coffeeshop.domain.model.InventoryItem;
import com.coffeeshop.domain.model.RecipeItem;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InMemoryRepository implements Repository {
    private final AtomicInteger orderId = new AtomicInteger(1);
    private final AtomicInteger orderItemId = new AtomicInteger(1);
    private final AtomicInteger paymentId = new AtomicInteger(1);
    private final AtomicInteger menuId = new AtomicInteger(25);
    private final AtomicInteger toppingId = new AtomicInteger(9);
    private final AtomicInteger userId = new AtomicInteger(4);
    private final List<User> users = new ArrayList<>();
    private final List<MenuItemRecord> menu = new ArrayList<>();
    private final List<Topping> toppings = new ArrayList<>();
    private final List<InventoryItem> inventory = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private final List<Payment> payments = new ArrayList<>();
    private final List<OrderStatusHistoryRecord> orderHistory = new ArrayList<>();
    private final List<InventoryTransactionRecord> inventoryTransactions = new ArrayList<>();
    private final Map<Integer, String> orderStatusById = new LinkedHashMap<>();
    private final AtomicInteger orderHistoryId = new AtomicInteger(1);
    private final AtomicInteger inventoryTransactionId = new AtomicInteger(1);

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
        menu.add(new MenuItemRecord(17, "Vanilla latte", 46000, "COFFEE", true));
        menu.add(new MenuItemRecord(18, "Caramel macchiato", 49000, "COFFEE", true));
        menu.add(new MenuItemRecord(19, "Mocha", 47000, "COFFEE", true));
        menu.add(new MenuItemRecord(20, "Hong tra sua", 39000, "TEA", true));
        menu.add(new MenuItemRecord(21, "Oolong sua", 41000, "TEA", true));
        menu.add(new MenuItemRecord(22, "Tra sen vang", 42000, "TEA", true));
        menu.add(new MenuItemRecord(23, "Matcha cream cheese", 55000, "MATCHA", true));
        menu.add(new MenuItemRecord(24, "Sinh to bo", 52000, "SMOOTHIE", true));

        toppings.add(new Topping(1, "Trân châu trắng", 10000, true));
        toppings.add(new Topping(2, "Pudding", 9000, true));
        toppings.add(new Topping(3, "Kem cheese", 12000, true));
        toppings.add(new Topping(4, "Extra shot", 8000, true));
        toppings.add(new Topping(5, "Kem muối", 7000, true));
        toppings.add(new Topping(6, "Thach cafe", 9000, true));
        toppings.add(new Topping(7, "Kem vani", 11000, true));
        toppings.add(new Topping(8, "Trân châu đường đen", 6000, true));

        inventory.add(new InventoryItem(1, "Coffee beans", "g", 5000, 500));
        inventory.add(new InventoryItem(2, "Fresh milk", "ml", 10000, 1000));
        inventory.add(new InventoryItem(3, "Tea leaves", "g", 3000, 300));
        inventory.add(new InventoryItem(4, "Peach syrup", "ml", 2500, 300));
        inventory.add(new InventoryItem(5, "Matcha powder", "g", 2000, 200));
        inventory.add(new InventoryItem(6, "Mango", "g", 5000, 500));
        inventory.add(new InventoryItem(7, "Pearl", "g", 4000, 400));
        inventory.add(new InventoryItem(8, "Cup L", "pcs", 300, 30));
        inventory.add(new InventoryItem(9, "Cream cheese", "g", 2500, 250));
        inventory.add(new InventoryItem(10, "Avocado", "g", 4500, 450));
        inventory.add(new InventoryItem(11, "Brown sugar syrup", "ml", 3000, 300));
        inventory.add(new InventoryItem(12, "Vanilla syrup", "ml", 2500, 250));
        inventory.add(new InventoryItem(13, "Pudding", "g", 3000, 300));
        inventory.add(new InventoryItem(14, "Coffee jelly", "g", 3000, 300));
        inventory.add(new InventoryItem(15, "Vanilla cream", "g", 2500, 250));
        inventory.add(new InventoryItem(16, "Salted cream", "g", 2500, 250));

        // Recipe items mapping:
        // 1: Coffee beans
        // 2: Fresh milk
        // 3: Tea leaves
        // 4: Peach syrup
        // 5: Matcha powder
        // 6: Mango
        // 7: Pearl
        // 8: Cup L
        // 9: Cream cheese
        // 10: Avocado
        // 11: Brown sugar syrup
        // 12: Vanilla syrup
        // 13: Pudding
        // 14: Coffee jelly
        // 15: Vanilla cream
        // 16: Salted cream
        recipeItems.add(new RecipeItem(1, 1, 18));
        recipeItems.add(new RecipeItem(2, 1, 15)); recipeItems.add(new RecipeItem(2, 2, 80));
        recipeItems.add(new RecipeItem(3, 3, 8)); recipeItems.add(new RecipeItem(3, 4, 40));
        recipeItems.add(new RecipeItem(4, 3, 10)); recipeItems.add(new RecipeItem(4, 2, 100));
        recipeItems.add(new RecipeItem(5, 5, 12)); recipeItems.add(new RecipeItem(5, 2, 120));
        recipeItems.add(new RecipeItem(6, 6, 150));
        recipeItems.add(new RecipeItem(7, 1, 18));
        recipeItems.add(new RecipeItem(8, 1, 16));
        recipeItems.add(new RecipeItem(9, 1, 18)); recipeItems.add(new RecipeItem(9, 2, 120));
        recipeItems.add(new RecipeItem(10, 1, 18)); recipeItems.add(new RecipeItem(10, 2, 120));
        recipeItems.add(new RecipeItem(11, 1, 22));
        recipeItems.add(new RecipeItem(12, 3, 8)); recipeItems.add(new RecipeItem(12, 4, 20));
        recipeItems.add(new RecipeItem(13, 3, 8));
        recipeItems.add(new RecipeItem(14, 5, 15)); recipeItems.add(new RecipeItem(14, 2, 100));
        recipeItems.add(new RecipeItem(15, 6, 120));
        recipeItems.add(new RecipeItem(16, 2, 150));
        // Default for 17-24 is 10g coffee beans for now, or match exactly what was in switch
        recipeItems.add(new RecipeItem(17, 1, 10));
        recipeItems.add(new RecipeItem(18, 1, 10));
        recipeItems.add(new RecipeItem(19, 1, 10));
        recipeItems.add(new RecipeItem(20, 1, 10));
        recipeItems.add(new RecipeItem(21, 1, 10));
        recipeItems.add(new RecipeItem(22, 1, 10));
        recipeItems.add(new RecipeItem(23, 1, 10));
        recipeItems.add(new RecipeItem(24, 1, 10));
    }

    public Optional<User> findUser(String username, String password) {
        return users.stream()
                .filter(user -> user.isActive() && user.getUsername().equals(username) && user.getPasswordHash().equals(password))
                .findFirst();
    }

    public List<User> getUsers() { return users; }
    public void saveUser(User user) {
        users.removeIf(existing -> existing.getId() == user.getId());
        users.add(user);
        users.sort(java.util.Comparator.comparingInt(User::getId));
    }
    public List<MenuItemRecord> getMenu() { return menu; }
    public void saveMenuItem(MenuItemRecord item) {
        menu.removeIf(existing -> existing.getId() == item.getId());
        menu.add(item);
        menu.sort(java.util.Comparator.comparingInt(MenuItemRecord::getId));
    }
    public List<Topping> getToppings() { return toppings; }
    public void saveTopping(Topping topping) {
        toppings.removeIf(existing -> existing.getId() == topping.getId());
        toppings.add(topping);
        toppings.sort(java.util.Comparator.comparingInt(Topping::getId));
    }
    public List<InventoryItem> getInventory() { return inventory; }
    public void saveInventoryItem(InventoryItem item) {
        inventory.removeIf(existing -> existing.getId() == item.getId());
        inventory.add(item);
        inventory.sort(java.util.Comparator.comparingInt(InventoryItem::getId));
    }
    public void adjustInventory(int inventoryItemId, double delta, Integer orderId, String reason) {
        InventoryItem item = inventory.stream()
                .filter(existing -> existing.getId() == inventoryItemId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Inventory item not found: " + inventoryItemId));
        item.setQuantity(item.getQuantity() + delta);
        inventoryTransactions.add(0, new InventoryTransactionRecord(
                inventoryTransactionId.getAndIncrement(),
                item.getId(),
                item.getName(),
                orderId,
                delta,
                item.getQuantity(),
                reason == null ? "MANUAL_UPDATE" : reason,
                LocalDateTime.now()
        ));
    }
    public List<Order> getOrders() { return orders; }
    public List<Payment> getPayments() { return payments; }
    public List<OrderStatusHistoryRecord> getOrderStatusHistory() { return orderHistory; }
    public List<InventoryTransactionRecord> getInventoryTransactions() { return inventoryTransactions; }
    public Optional<String> getStoragePath() { return Optional.empty(); }
    public int nextOrderId() { return orderId.getAndIncrement(); }
    public int nextOrderItemId() { return orderItemId.getAndIncrement(); }
    public int nextPaymentId() { return paymentId.getAndIncrement(); }
    public int nextMenuId() { return menuId.getAndIncrement(); }
    public int nextToppingId() { return toppingId.getAndIncrement(); }
    public int nextUserId() { return userId.getAndIncrement(); }
    public void saveOrder(Order order) {
        orders.removeIf(existing -> existing.getId() == order.getId());
        orders.add(order);
        orders.sort(java.util.Comparator.comparingInt(Order::getId));

        String previousStatus = orderStatusById.get(order.getId());
        if (!Objects.equals(previousStatus, order.getStatus())) {
            orderHistory.add(0, new OrderStatusHistoryRecord(
                    orderHistoryId.getAndIncrement(),
                    order.getId(),
                    order.getStatus(),
                    previousStatus == null ? "Order created" : "State changed",
                    LocalDateTime.now()
            ));
            orderStatusById.put(order.getId(), order.getStatus());
        }
    }
    public void savePayment(Payment payment) {
        payments.removeIf(existing -> existing.getId() == payment.getId());
        payments.add(payment);
        payments.sort(java.util.Comparator.comparingInt(Payment::getId));
    }
    public void backupTo(String destinationPath) {
        throw new UnsupportedOperationException("In-memory repository does not support backup.");
    }

    // Recipe Item logic
    private final List<RecipeItem> recipeItems = new ArrayList<>();

    @Override
    public List<RecipeItem> getRecipeItems(int beverageId) {
        return recipeItems.stream()
                .filter(r -> r.getBeverageId() == beverageId)
                .collect(Collectors.toList());
    }

    @Override
    public void saveRecipeItem(RecipeItem item) {
        recipeItems.removeIf(r -> r.getBeverageId() == item.getBeverageId() && r.getInventoryItemId() == item.getInventoryItemId());
        recipeItems.add(item);
    }

    @Override
    public void deleteRecipeItem(int beverageId, int inventoryItemId) {
        recipeItems.removeIf(r -> r.getBeverageId() == beverageId && r.getInventoryItemId() == inventoryItemId);
    }
}
