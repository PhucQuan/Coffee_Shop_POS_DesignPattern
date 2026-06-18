package com.coffeeshop.infrastructure;

import com.coffeeshop.domain.model.InventoryItem;
import com.coffeeshop.domain.model.Order;
import com.coffeeshop.domain.model.Payment;
import com.coffeeshop.domain.model.RecipeItem;
import com.coffeeshop.domain.model.Topping;
import com.coffeeshop.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface Repository {
    Optional<User> findUser(String username, String password);
    List<User> getUsers();
    List<MenuItemRecord> getMenu();
    List<Topping> getToppings();
    List<InventoryItem> getInventory();
    List<Order> getOrders();
    List<Payment> getPayments();
    
    int nextOrderId();
    int nextOrderItemId();
    int nextPaymentId();
    int nextMenuId();
    int nextToppingId();
    int nextUserId();
    
    void saveOrder(Order order);
    void savePayment(Payment payment);

    // For Recipe
    List<RecipeItem> getRecipeItems(int beverageId);
    void saveRecipeItem(RecipeItem item);
    void deleteRecipeItem(int beverageId, int inventoryItemId);
}
