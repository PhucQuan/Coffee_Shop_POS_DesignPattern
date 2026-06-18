package com.coffeeshop.infrastructure;

import com.coffeeshop.domain.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteRepository implements Repository {
    private final Connection conn;

    public SqliteRepository(String dbPath) throws SQLException {
        this.conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        initSchema();
        seedIfEmpty();
    }

    private void initSchema() throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, username TEXT, password_hash TEXT, role TEXT, active INTEGER)");
            stmt.execute("CREATE TABLE IF NOT EXISTS beverages (id INTEGER PRIMARY KEY, name TEXT, base_price REAL, category TEXT, active INTEGER)");
            stmt.execute("CREATE TABLE IF NOT EXISTS toppings (id INTEGER PRIMARY KEY, name TEXT, extra_price REAL, active INTEGER)");
            stmt.execute("CREATE TABLE IF NOT EXISTS inventory_items (id INTEGER PRIMARY KEY, name TEXT, unit TEXT, quantity REAL, reorder_level REAL)");
            stmt.execute("CREATE TABLE IF NOT EXISTS recipe_items (beverage_id INTEGER, inventory_item_id INTEGER, quantity_required REAL, PRIMARY KEY (beverage_id, inventory_item_id))");
            stmt.execute("CREATE TABLE IF NOT EXISTS orders (id INTEGER PRIMARY KEY, created_at TEXT, status TEXT, discount_type TEXT, discount_amount REAL, total_amount REAL, inventory_deducted INTEGER)");
            stmt.execute("CREATE TABLE IF NOT EXISTS order_items (id INTEGER PRIMARY KEY, order_id INTEGER, beverage_id INTEGER, quantity INTEGER, note TEXT, item_price REAL)");
            stmt.execute("CREATE TABLE IF NOT EXISTS payments (id INTEGER PRIMARY KEY, order_id INTEGER, method TEXT, amount REAL, transaction_code TEXT, status TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS settings (key TEXT PRIMARY KEY, value TEXT)");
        }
    }

    private void seedIfEmpty() throws SQLException {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            if (rs.next() && rs.getInt(1) > 0) return;
        }

        // Seed Users
        executeInsert("INSERT INTO users VALUES (1, 'admin', '123', 'ADMIN', 1)");
        executeInsert("INSERT INTO users VALUES (2, 'cashier01', '123', 'CASHIER', 1)");
        executeInsert("INSERT INTO users VALUES (3, 'kitchen01', '123', 'KITCHEN', 1)");

        // Seed Beverages
        executeInsert("INSERT INTO beverages VALUES (1, 'Ca phe sua', 30000, 'COFFEE', 1)");
        executeInsert("INSERT INTO beverages VALUES (2, 'Bac xiu', 32000, 'COFFEE', 1)");
        executeInsert("INSERT INTO beverages VALUES (3, 'Tra dao', 35000, 'TEA', 1)");
        executeInsert("INSERT INTO beverages VALUES (4, 'Tra sua', 38000, 'TEA', 1)");
        executeInsert("INSERT INTO beverages VALUES (5, 'Matcha latte', 42000, 'MATCHA', 1)");
        executeInsert("INSERT INTO beverages VALUES (6, 'Sinh to xoai', 45000, 'SMOOTHIE', 1)");
        executeInsert("INSERT INTO beverages VALUES (7, 'Espresso', 28000, 'COFFEE', 1)");
        executeInsert("INSERT INTO beverages VALUES (8, 'Americano', 30000, 'COFFEE', 1)");
        executeInsert("INSERT INTO beverages VALUES (9, 'Latte', 42000, 'COFFEE', 1)");
        executeInsert("INSERT INTO beverages VALUES (10, 'Cappuccino', 42000, 'COFFEE', 1)");
        executeInsert("INSERT INTO beverages VALUES (11, 'Cold brew', 45000, 'COFFEE', 1)");
        executeInsert("INSERT INTO beverages VALUES (12, 'Tra vai', 39000, 'TEA', 1)");
        executeInsert("INSERT INTO beverages VALUES (13, 'Tra tac mat ong', 34000, 'TEA', 1)");
        executeInsert("INSERT INTO beverages VALUES (14, 'Matcha da xay', 52000, 'MATCHA', 1)");
        executeInsert("INSERT INTO beverages VALUES (15, 'Sinh to dau', 48000, 'SMOOTHIE', 1)");
        executeInsert("INSERT INTO beverages VALUES (16, 'Cacao nong', 36000, 'COFFEE', 1)");
        executeInsert("INSERT INTO beverages VALUES (17, 'Vanilla latte', 46000, 'COFFEE', 1)");
        executeInsert("INSERT INTO beverages VALUES (18, 'Caramel macchiato', 49000, 'COFFEE', 1)");
        executeInsert("INSERT INTO beverages VALUES (19, 'Mocha', 47000, 'COFFEE', 1)");
        executeInsert("INSERT INTO beverages VALUES (20, 'Hong tra sua', 39000, 'TEA', 1)");
        executeInsert("INSERT INTO beverages VALUES (21, 'Oolong sua', 41000, 'TEA', 1)");
        executeInsert("INSERT INTO beverages VALUES (22, 'Tra sen vang', 42000, 'TEA', 1)");
        executeInsert("INSERT INTO beverages VALUES (23, 'Matcha cream cheese', 55000, 'MATCHA', 1)");
        executeInsert("INSERT INTO beverages VALUES (24, 'Sinh to bo', 52000, 'SMOOTHIE', 1)");

        // Seed Toppings
        executeInsert("INSERT INTO toppings VALUES (1, 'Tran chau', 10000, 1)");
        executeInsert("INSERT INTO toppings VALUES (2, 'Pudding', 9000, 1)");
        executeInsert("INSERT INTO toppings VALUES (3, 'Kem cheese', 12000, 1)");
        executeInsert("INSERT INTO toppings VALUES (4, 'Extra shot', 8000, 1)");
        executeInsert("INSERT INTO toppings VALUES (5, 'Size L', 7000, 1)");
        executeInsert("INSERT INTO toppings VALUES (6, 'Thach cafe', 9000, 1)");
        executeInsert("INSERT INTO toppings VALUES (7, 'Kem vani', 11000, 1)");
        executeInsert("INSERT INTO toppings VALUES (8, 'Duong den', 6000, 1)");

        // Seed Inventory
        executeInsert("INSERT INTO inventory_items VALUES (1, 'Coffee beans', 'g', 5000, 500)");
        executeInsert("INSERT INTO inventory_items VALUES (2, 'Fresh milk', 'ml', 10000, 1000)");
        executeInsert("INSERT INTO inventory_items VALUES (3, 'Tea leaves', 'g', 3000, 300)");
        executeInsert("INSERT INTO inventory_items VALUES (4, 'Peach syrup', 'ml', 2500, 300)");
        executeInsert("INSERT INTO inventory_items VALUES (5, 'Matcha powder', 'g', 2000, 200)");
        executeInsert("INSERT INTO inventory_items VALUES (6, 'Mango', 'g', 5000, 500)");
        executeInsert("INSERT INTO inventory_items VALUES (7, 'Pearl', 'g', 4000, 400)");
        executeInsert("INSERT INTO inventory_items VALUES (8, 'Cup L', 'pcs', 300, 30)");
        executeInsert("INSERT INTO inventory_items VALUES (9, 'Cream cheese', 'g', 2500, 250)");
        executeInsert("INSERT INTO inventory_items VALUES (10, 'Avocado', 'g', 4500, 450)");
        executeInsert("INSERT INTO inventory_items VALUES (11, 'Brown sugar syrup', 'ml', 3000, 300)");
        executeInsert("INSERT INTO inventory_items VALUES (12, 'Vanilla syrup', 'ml', 2500, 250)");

        // Seed Recipe items (minimal version mapping)
        executeInsert("INSERT INTO recipe_items VALUES (1, 1, 18)");
        executeInsert("INSERT INTO recipe_items VALUES (2, 1, 15)");
        executeInsert("INSERT INTO recipe_items VALUES (2, 2, 80)");
        executeInsert("INSERT INTO recipe_items VALUES (3, 3, 8)");
        executeInsert("INSERT INTO recipe_items VALUES (3, 4, 40)");
        executeInsert("INSERT INTO recipe_items VALUES (4, 3, 10)");
        executeInsert("INSERT INTO recipe_items VALUES (4, 2, 100)");
        executeInsert("INSERT INTO recipe_items VALUES (5, 5, 12)");
        executeInsert("INSERT INTO recipe_items VALUES (5, 2, 120)");
        executeInsert("INSERT INTO recipe_items VALUES (6, 6, 150)");
        // Add minimal settings
        executeInsert("INSERT INTO settings VALUES ('store_name', 'PurrCoffee')");
    }

    private void executeInsert(String sql) {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<User> findUser(String username, String password) {
        return getUsers().stream()
                .filter(u -> u.getUsername().equals(username) && u.getPasswordHash().equals(password) && u.isActive())
                .findFirst();
    }

    @Override
    public List<User> getUsers() {
        List<User> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {
            while (rs.next()) {
                list.add(new User(rs.getInt("id"), rs.getString("username"), rs.getString("password_hash"), rs.getString("role"), rs.getInt("active") == 1));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<MenuItemRecord> getMenu() {
        List<MenuItemRecord> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM beverages")) {
            while (rs.next()) {
                list.add(new MenuItemRecord(rs.getInt("id"), rs.getString("name"), rs.getDouble("base_price"), rs.getString("category"), rs.getInt("active") == 1));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<Topping> getToppings() {
        List<Topping> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM toppings")) {
            while (rs.next()) {
                list.add(new Topping(rs.getInt("id"), rs.getString("name"), rs.getDouble("extra_price"), rs.getInt("active") == 1));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<InventoryItem> getInventory() {
        List<InventoryItem> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM inventory_items")) {
            while (rs.next()) {
                list.add(new InventoryItem(rs.getInt("id"), rs.getString("name"), rs.getString("unit"), rs.getDouble("quantity"), rs.getDouble("reorder_level")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<Order> getOrders() {
        // Implement complex join or simple fetch
        return new ArrayList<>(); // To be completed
    }

    @Override
    public List<Payment> getPayments() {
        return new ArrayList<>(); // To be completed
    }

    @Override
    public int nextOrderId() { return 1; /* Replace with auto_increment */ }
    @Override
    public int nextOrderItemId() { return 1; }
    @Override
    public int nextPaymentId() { return 1; }
    @Override
    public int nextMenuId() { return 1; }
    @Override
    public int nextToppingId() { return 1; }
    @Override
    public int nextUserId() { return 1; }

    @Override
    public void saveOrder(Order order) {}
    @Override
    public void savePayment(Payment payment) {}

    @Override
    public List<RecipeItem> getRecipeItems(int beverageId) {
        List<RecipeItem> list = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM recipe_items WHERE beverage_id = ?")) {
            stmt.setInt(1, beverageId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new RecipeItem(rs.getInt("beverage_id"), rs.getInt("inventory_item_id"), rs.getDouble("quantity_required")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public void saveRecipeItem(RecipeItem item) {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT OR REPLACE INTO recipe_items (beverage_id, inventory_item_id, quantity_required) VALUES (?, ?, ?)")) {
            stmt.setInt(1, item.getBeverageId());
            stmt.setInt(2, item.getInventoryItemId());
            stmt.setDouble(3, item.getQuantityRequired());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void deleteRecipeItem(int beverageId, int inventoryItemId) {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM recipe_items WHERE beverage_id = ? AND inventory_item_id = ?")) {
            stmt.setInt(1, beverageId);
            stmt.setInt(2, inventoryItemId);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
