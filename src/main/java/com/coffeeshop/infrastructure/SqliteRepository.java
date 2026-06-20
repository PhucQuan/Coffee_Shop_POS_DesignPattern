package com.coffeeshop.infrastructure;

import com.coffeeshop.domain.model.InventoryItem;
import com.coffeeshop.domain.model.Order;
import com.coffeeshop.domain.model.OrderItem;
import com.coffeeshop.domain.model.Payment;
import com.coffeeshop.domain.model.RecipeItem;
import com.coffeeshop.domain.model.Topping;
import com.coffeeshop.domain.model.User;
import com.coffeeshop.domain.patterns.decorator.Beverage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SqliteRepository implements Repository, AutoCloseable {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final Connection conn;

    public SqliteRepository(String dbPath) throws SQLException {
        this.conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        enablePragmas();
        initSchema();
        migrateSchema();
        seedIfEmpty();
    }

    private void enablePragmas() throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
    }

    private void initSchema() throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY,
                        username TEXT NOT NULL UNIQUE,
                        password_hash TEXT NOT NULL,
                        role TEXT NOT NULL,
                        active INTEGER NOT NULL
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS beverages (
                        id INTEGER PRIMARY KEY,
                        name TEXT NOT NULL UNIQUE,
                        base_price REAL NOT NULL,
                        category TEXT NOT NULL,
                        active INTEGER NOT NULL
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS toppings (
                        id INTEGER PRIMARY KEY,
                        name TEXT NOT NULL UNIQUE,
                        extra_price REAL NOT NULL,
                        active INTEGER NOT NULL
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS inventory_items (
                        id INTEGER PRIMARY KEY,
                        name TEXT NOT NULL UNIQUE,
                        unit TEXT NOT NULL,
                        quantity REAL NOT NULL,
                        reorder_level REAL NOT NULL
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS recipe_items (
                        beverage_id INTEGER NOT NULL,
                        inventory_item_id INTEGER NOT NULL,
                        quantity_required REAL NOT NULL,
                        PRIMARY KEY (beverage_id, inventory_item_id),
                        FOREIGN KEY (beverage_id) REFERENCES beverages(id),
                        FOREIGN KEY (inventory_item_id) REFERENCES inventory_items(id)
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS orders (
                        id INTEGER PRIMARY KEY,
                        created_at TEXT NOT NULL,
                        status TEXT NOT NULL,
                        discount_type TEXT,
                        discount_amount REAL NOT NULL DEFAULT 0,
                        total_amount REAL NOT NULL DEFAULT 0,
                        inventory_deducted INTEGER NOT NULL DEFAULT 0
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS order_items (
                        id INTEGER PRIMARY KEY,
                        order_id INTEGER NOT NULL,
                        beverage_id INTEGER NOT NULL,
                        beverage_description TEXT,
                        quantity INTEGER NOT NULL,
                        note TEXT,
                        unit_price REAL NOT NULL DEFAULT 0,
                        item_price REAL NOT NULL,
                        FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                        FOREIGN KEY (beverage_id) REFERENCES beverages(id)
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS order_item_toppings (
                        order_item_id INTEGER NOT NULL,
                        topping_id INTEGER NOT NULL,
                        PRIMARY KEY (order_item_id, topping_id),
                        FOREIGN KEY (order_item_id) REFERENCES order_items(id) ON DELETE CASCADE,
                        FOREIGN KEY (topping_id) REFERENCES toppings(id)
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS payments (
                        id INTEGER PRIMARY KEY,
                        order_id INTEGER NOT NULL UNIQUE,
                        method TEXT NOT NULL,
                        amount REAL NOT NULL,
                        transaction_code TEXT,
                        status TEXT NOT NULL,
                        created_at TEXT NOT NULL,
                        FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS order_status_history (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        order_id INTEGER NOT NULL,
                        status TEXT NOT NULL,
                        note TEXT,
                        changed_at TEXT NOT NULL,
                        FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS inventory_transactions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        inventory_item_id INTEGER NOT NULL,
                        order_id INTEGER,
                        change_amount REAL NOT NULL,
                        balance_after REAL NOT NULL,
                        reason TEXT NOT NULL,
                        created_at TEXT NOT NULL,
                        FOREIGN KEY (inventory_item_id) REFERENCES inventory_items(id),
                        FOREIGN KEY (order_id) REFERENCES orders(id)
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS settings (
                        key TEXT PRIMARY KEY,
                        value TEXT
                    )
                    """);

            stmt.execute("CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_inventory_transactions_item_id ON inventory_transactions(inventory_item_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_order_status_history_order_id ON order_status_history(order_id)");
        }
    }

    private void migrateSchema() throws SQLException {
        ensureColumnExists("orders", "inventory_deducted", "INTEGER NOT NULL DEFAULT 0");
        ensureColumnExists("order_items", "beverage_description", "TEXT");
        ensureColumnExists("order_items", "unit_price", "REAL NOT NULL DEFAULT 0");
        ensureColumnExists("payments", "created_at", "TEXT");

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("UPDATE payments SET created_at = COALESCE(created_at, datetime('now'))");
        }
    }

    private void ensureColumnExists(String tableName, String columnName, String definition) throws SQLException {
        if (hasColumn(tableName, columnName)) {
            return;
        }
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + definition);
        }
    }

    private boolean hasColumn(String tableName, String columnName) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("PRAGMA table_info(" + tableName + ")");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                if (columnName.equalsIgnoreCase(rs.getString("name"))) {
                    return true;
                }
            }
            return false;
        }
    }

    private void seedIfEmpty() throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        }

        try {
            conn.setAutoCommit(false);

            executeSeed("INSERT INTO users VALUES (1, 'admin', '123', 'ADMIN', 1)");
            executeSeed("INSERT INTO users VALUES (2, 'cashier01', '123', 'CASHIER', 1)");
            executeSeed("INSERT INTO users VALUES (3, 'kitchen01', '123', 'KITCHEN', 1)");

            executeSeed("INSERT INTO beverages VALUES (1, 'Ca phe sua', 30000, 'COFFEE', 1)");
            executeSeed("INSERT INTO beverages VALUES (2, 'Bac xiu', 32000, 'COFFEE', 1)");
            executeSeed("INSERT INTO beverages VALUES (3, 'Tra dao', 35000, 'TEA', 1)");
            executeSeed("INSERT INTO beverages VALUES (4, 'Tra sua', 38000, 'TEA', 1)");
            executeSeed("INSERT INTO beverages VALUES (5, 'Matcha latte', 42000, 'MATCHA', 1)");
            executeSeed("INSERT INTO beverages VALUES (6, 'Sinh to xoai', 45000, 'SMOOTHIE', 1)");
            executeSeed("INSERT INTO beverages VALUES (7, 'Espresso', 28000, 'COFFEE', 1)");
            executeSeed("INSERT INTO beverages VALUES (8, 'Americano', 30000, 'COFFEE', 1)");
            executeSeed("INSERT INTO beverages VALUES (9, 'Latte', 42000, 'COFFEE', 1)");
            executeSeed("INSERT INTO beverages VALUES (10, 'Cappuccino', 42000, 'COFFEE', 1)");
            executeSeed("INSERT INTO beverages VALUES (11, 'Cold brew', 45000, 'COFFEE', 1)");
            executeSeed("INSERT INTO beverages VALUES (12, 'Tra vai', 39000, 'TEA', 1)");
            executeSeed("INSERT INTO beverages VALUES (13, 'Tra tac mat ong', 34000, 'TEA', 1)");
            executeSeed("INSERT INTO beverages VALUES (14, 'Matcha da xay', 52000, 'MATCHA', 1)");
            executeSeed("INSERT INTO beverages VALUES (15, 'Sinh to dau', 48000, 'SMOOTHIE', 1)");
            executeSeed("INSERT INTO beverages VALUES (16, 'Cacao nong', 36000, 'COFFEE', 1)");
            executeSeed("INSERT INTO beverages VALUES (17, 'Vanilla latte', 46000, 'COFFEE', 1)");
            executeSeed("INSERT INTO beverages VALUES (18, 'Caramel macchiato', 49000, 'COFFEE', 1)");
            executeSeed("INSERT INTO beverages VALUES (19, 'Mocha', 47000, 'COFFEE', 1)");
            executeSeed("INSERT INTO beverages VALUES (20, 'Hong tra sua', 39000, 'TEA', 1)");
            executeSeed("INSERT INTO beverages VALUES (21, 'Oolong sua', 41000, 'TEA', 1)");
            executeSeed("INSERT INTO beverages VALUES (22, 'Tra sen vang', 42000, 'TEA', 1)");
            executeSeed("INSERT INTO beverages VALUES (23, 'Matcha cream cheese', 55000, 'MATCHA', 1)");
            executeSeed("INSERT INTO beverages VALUES (24, 'Sinh to bo', 52000, 'SMOOTHIE', 1)");

            executeSeed("INSERT INTO toppings VALUES (1, 'Tran chau', 10000, 1)");
            executeSeed("INSERT INTO toppings VALUES (2, 'Pudding', 9000, 1)");
            executeSeed("INSERT INTO toppings VALUES (3, 'Kem cheese', 12000, 1)");
            executeSeed("INSERT INTO toppings VALUES (4, 'Extra shot', 8000, 1)");
            executeSeed("INSERT INTO toppings VALUES (5, 'Size L', 7000, 1)");
            executeSeed("INSERT INTO toppings VALUES (6, 'Thach cafe', 9000, 1)");
            executeSeed("INSERT INTO toppings VALUES (7, 'Kem vani', 11000, 1)");
            executeSeed("INSERT INTO toppings VALUES (8, 'Duong den', 6000, 1)");

            executeSeed("INSERT INTO inventory_items VALUES (1, 'Coffee beans', 'g', 5000, 500)");
            executeSeed("INSERT INTO inventory_items VALUES (2, 'Fresh milk', 'ml', 10000, 1000)");
            executeSeed("INSERT INTO inventory_items VALUES (3, 'Tea leaves', 'g', 3000, 300)");
            executeSeed("INSERT INTO inventory_items VALUES (4, 'Peach syrup', 'ml', 2500, 300)");
            executeSeed("INSERT INTO inventory_items VALUES (5, 'Matcha powder', 'g', 2000, 200)");
            executeSeed("INSERT INTO inventory_items VALUES (6, 'Mango', 'g', 5000, 500)");
            executeSeed("INSERT INTO inventory_items VALUES (7, 'Pearl', 'g', 4000, 400)");
            executeSeed("INSERT INTO inventory_items VALUES (8, 'Cup L', 'pcs', 300, 30)");
            executeSeed("INSERT INTO inventory_items VALUES (9, 'Cream cheese', 'g', 2500, 250)");
            executeSeed("INSERT INTO inventory_items VALUES (10, 'Avocado', 'g', 4500, 450)");
            executeSeed("INSERT INTO inventory_items VALUES (11, 'Brown sugar syrup', 'ml', 3000, 300)");
            executeSeed("INSERT INTO inventory_items VALUES (12, 'Vanilla syrup', 'ml', 2500, 250)");

            executeSeed("INSERT INTO recipe_items VALUES (1, 1, 18)");
            executeSeed("INSERT INTO recipe_items VALUES (2, 1, 15)");
            executeSeed("INSERT INTO recipe_items VALUES (2, 2, 80)");
            executeSeed("INSERT INTO recipe_items VALUES (3, 3, 8)");
            executeSeed("INSERT INTO recipe_items VALUES (3, 4, 40)");
            executeSeed("INSERT INTO recipe_items VALUES (4, 3, 10)");
            executeSeed("INSERT INTO recipe_items VALUES (4, 2, 100)");
            executeSeed("INSERT INTO recipe_items VALUES (5, 5, 12)");
            executeSeed("INSERT INTO recipe_items VALUES (5, 2, 120)");
            executeSeed("INSERT INTO recipe_items VALUES (6, 6, 150)");
            executeSeed("INSERT INTO recipe_items VALUES (7, 1, 18)");
            executeSeed("INSERT INTO recipe_items VALUES (8, 1, 16)");
            executeSeed("INSERT INTO recipe_items VALUES (9, 1, 18)");
            executeSeed("INSERT INTO recipe_items VALUES (9, 2, 120)");
            executeSeed("INSERT INTO recipe_items VALUES (10, 1, 18)");
            executeSeed("INSERT INTO recipe_items VALUES (10, 2, 120)");
            executeSeed("INSERT INTO recipe_items VALUES (11, 1, 22)");
            executeSeed("INSERT INTO recipe_items VALUES (12, 3, 8)");
            executeSeed("INSERT INTO recipe_items VALUES (12, 4, 20)");
            executeSeed("INSERT INTO recipe_items VALUES (13, 3, 8)");
            executeSeed("INSERT INTO recipe_items VALUES (14, 5, 15)");
            executeSeed("INSERT INTO recipe_items VALUES (14, 2, 100)");
            executeSeed("INSERT INTO recipe_items VALUES (15, 6, 120)");
            executeSeed("INSERT INTO recipe_items VALUES (16, 2, 150)");
            executeSeed("INSERT INTO recipe_items VALUES (17, 1, 10)");
            executeSeed("INSERT INTO recipe_items VALUES (18, 1, 10)");
            executeSeed("INSERT INTO recipe_items VALUES (19, 1, 10)");
            executeSeed("INSERT INTO recipe_items VALUES (20, 1, 10)");
            executeSeed("INSERT INTO recipe_items VALUES (21, 1, 10)");
            executeSeed("INSERT INTO recipe_items VALUES (22, 1, 10)");
            executeSeed("INSERT INTO recipe_items VALUES (23, 1, 10)");
            executeSeed("INSERT INTO recipe_items VALUES (24, 1, 10)");

            executeSeed("INSERT INTO settings VALUES ('store_name', 'PurrCoffee POS')");
            executeSeed("INSERT INTO settings VALUES ('tax_rate', '0.10')");
            executeSeed("INSERT INTO settings VALUES ('address', '123 Coffee Street')");

            conn.commit();
        } catch (SQLException ex) {
            rollbackQuietly();
            throw ex;
        } finally {
            resetAutoCommit();
        }
    }

    private void executeSeed(String sql) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    @Override
    public Optional<User> findUser(String username, String password) {
        try (PreparedStatement stmt = conn.prepareStatement("""
                SELECT id, username, password_hash, role, active
                FROM users
                WHERE username = ? AND password_hash = ? AND active = 1
                LIMIT 1
                """)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUser(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to load user.", ex);
        }
    }

    @Override
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement("""
                SELECT id, username, password_hash, role, active
                FROM users
                ORDER BY id
                """);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(mapUser(rs));
            }
            return users;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to load users.", ex);
        }
    }

    @Override
    public void saveUser(User user) {
        try (PreparedStatement stmt = conn.prepareStatement("""
                INSERT INTO users (id, username, password_hash, role, active)
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT(id) DO UPDATE SET
                    username = excluded.username,
                    password_hash = excluded.password_hash,
                    role = excluded.role,
                    active = excluded.active
                """)) {
            stmt.setInt(1, user.getId());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole());
            stmt.setInt(5, user.isActive() ? 1 : 0);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to save user " + user.getUsername(), ex);
        }
    }

    @Override
    public List<MenuItemRecord> getMenu() {
        List<MenuItemRecord> items = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement("""
                SELECT id, name, base_price, category, active
                FROM beverages
                ORDER BY id
                """);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                items.add(new MenuItemRecord(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("base_price"),
                        rs.getString("category"),
                        rs.getInt("active") == 1
                ));
            }
            return items;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to load menu.", ex);
        }
    }

    @Override
    public void saveMenuItem(MenuItemRecord item) {
        try (PreparedStatement stmt = conn.prepareStatement("""
                INSERT INTO beverages (id, name, base_price, category, active)
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT(id) DO UPDATE SET
                    name = excluded.name,
                    base_price = excluded.base_price,
                    category = excluded.category,
                    active = excluded.active
                """)) {
            stmt.setInt(1, item.getId());
            stmt.setString(2, item.getName());
            stmt.setDouble(3, item.getBasePrice());
            stmt.setString(4, item.getCategory());
            stmt.setInt(5, item.isActive() ? 1 : 0);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to save beverage " + item.getName(), ex);
        }
    }

    @Override
    public List<Topping> getToppings() {
        List<Topping> toppings = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement("""
                SELECT id, name, extra_price, active
                FROM toppings
                ORDER BY id
                """);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                toppings.add(new Topping(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("extra_price"),
                        rs.getInt("active") == 1
                ));
            }
            return toppings;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to load toppings.", ex);
        }
    }

    @Override
    public void saveTopping(Topping topping) {
        try (PreparedStatement stmt = conn.prepareStatement("""
                INSERT INTO toppings (id, name, extra_price, active)
                VALUES (?, ?, ?, ?)
                ON CONFLICT(id) DO UPDATE SET
                    name = excluded.name,
                    extra_price = excluded.extra_price,
                    active = excluded.active
                """)) {
            stmt.setInt(1, topping.getId());
            stmt.setString(2, topping.getName());
            stmt.setDouble(3, topping.getExtraPrice());
            stmt.setInt(4, topping.isActive() ? 1 : 0);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to save topping " + topping.getName(), ex);
        }
    }

    @Override
    public List<InventoryItem> getInventory() {
        List<InventoryItem> inventory = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement("""
                SELECT id, name, unit, quantity, reorder_level
                FROM inventory_items
                ORDER BY id
                """);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                inventory.add(new InventoryItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("unit"),
                        rs.getDouble("quantity"),
                        rs.getDouble("reorder_level")
                ));
            }
            return inventory;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to load inventory.", ex);
        }
    }

    @Override
    public void saveInventoryItem(InventoryItem item) {
        try (PreparedStatement stmt = conn.prepareStatement("""
                INSERT INTO inventory_items (id, name, unit, quantity, reorder_level)
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT(id) DO UPDATE SET
                    name = excluded.name,
                    unit = excluded.unit,
                    quantity = excluded.quantity,
                    reorder_level = excluded.reorder_level
                """)) {
            stmt.setInt(1, item.getId());
            stmt.setString(2, item.getName());
            stmt.setString(3, item.getUnit());
            stmt.setDouble(4, item.getQuantity());
            stmt.setDouble(5, item.getReorderLevel());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to save inventory item " + item.getName(), ex);
        }
    }

    @Override
    public void adjustInventory(int inventoryItemId, double delta, Integer orderId, String reason) {
        try {
            conn.setAutoCommit(false);

            InventoryItem item = findInventoryItemById(inventoryItemId);
            double updatedQuantity = item.getQuantity() + delta;
            if (updatedQuantity < 0) {
                throw new IllegalArgumentException("Inventory cannot go below 0 for " + item.getName());
            }

            try (PreparedStatement update = conn.prepareStatement("""
                    UPDATE inventory_items
                    SET quantity = ?
                    WHERE id = ?
                    """)) {
                update.setDouble(1, updatedQuantity);
                update.setInt(2, inventoryItemId);
                update.executeUpdate();
            }

            try (PreparedStatement insert = conn.prepareStatement("""
                    INSERT INTO inventory_transactions (inventory_item_id, order_id, change_amount, balance_after, reason, created_at)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """)) {
                insert.setInt(1, inventoryItemId);
                if (orderId == null) {
                    insert.setNull(2, java.sql.Types.INTEGER);
                } else {
                    insert.setInt(2, orderId);
                }
                insert.setDouble(3, delta);
                insert.setDouble(4, updatedQuantity);
                insert.setString(5, reason == null ? "MANUAL_UPDATE" : reason);
                insert.setString(6, nowText());
                insert.executeUpdate();
            }

            conn.commit();
        } catch (SQLException ex) {
            rollbackQuietly();
            throw new RuntimeException("Failed to adjust inventory item " + inventoryItemId, ex);
        } finally {
            resetAutoCommit();
        }
    }

    @Override
    public List<Order> getOrders() {
        Map<Integer, Payment> paymentByOrderId = getPayments().stream()
                .collect(Collectors.toMap(Payment::getOrderId, payment -> payment, (left, right) -> right, LinkedHashMap::new));
        List<Order> orders = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement("""
                SELECT id, created_at, status, discount_type, discount_amount, total_amount, inventory_deducted
                FROM orders
                ORDER BY id
                """);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("id"),
                        parseDateTime(rs.getString("created_at")),
                        rs.getString("status"),
                        rs.getString("discount_type"),
                        rs.getDouble("discount_amount"),
                        rs.getDouble("total_amount"),
                        rs.getInt("inventory_deducted") == 1
                );
                loadOrderItems(order);
                Payment payment = paymentByOrderId.get(order.getId());
                if (payment != null) {
                    order.setPayment(payment);
                }
                orders.add(order);
            }
            return orders;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to load orders.", ex);
        }
    }

    @Override
    public List<Payment> getPayments() {
        List<Payment> payments = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement("""
                SELECT id, order_id, method, amount, transaction_code, status
                FROM payments
                ORDER BY id
                """);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                payments.add(new Payment(
                        rs.getInt("id"),
                        rs.getInt("order_id"),
                        rs.getString("method"),
                        rs.getDouble("amount"),
                        rs.getString("transaction_code"),
                        rs.getString("status")
                ));
            }
            return payments;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to load payments.", ex);
        }
    }

    @Override
    public int nextOrderId() {
        return nextId("orders");
    }

    @Override
    public int nextOrderItemId() {
        return nextId("order_items");
    }

    @Override
    public int nextPaymentId() {
        return nextId("payments");
    }

    @Override
    public int nextMenuId() {
        return nextId("beverages");
    }

    @Override
    public int nextToppingId() {
        return nextId("toppings");
    }

    @Override
    public int nextUserId() {
        return nextId("users");
    }

    @Override
    public void saveOrder(Order order) {
        String previousStatus = findPersistedOrderStatus(order.getId());
        Map<String, Integer> toppingIdsByName = getToppings().stream()
                .collect(Collectors.toMap(
                        topping -> topping.getName().toLowerCase(Locale.ROOT),
                        Topping::getId,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement("""
                    INSERT INTO orders (id, created_at, status, discount_type, discount_amount, total_amount, inventory_deducted)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    ON CONFLICT(id) DO UPDATE SET
                        created_at = excluded.created_at,
                        status = excluded.status,
                        discount_type = excluded.discount_type,
                        discount_amount = excluded.discount_amount,
                        total_amount = excluded.total_amount,
                        inventory_deducted = excluded.inventory_deducted
                    """)) {
                stmt.setInt(1, order.getId());
                stmt.setString(2, order.getCreatedAt().format(DATE_TIME_FORMATTER));
                stmt.setString(3, order.getStatus());
                stmt.setString(4, order.getDiscountType());
                stmt.setDouble(5, order.getDiscountAmount());
                stmt.setDouble(6, order.getTotalAmount());
                stmt.setInt(7, order.isInventoryDeducted() ? 1 : 0);
                stmt.executeUpdate();
            }

            try (PreparedStatement deleteLinks = conn.prepareStatement("""
                    DELETE FROM order_item_toppings
                    WHERE order_item_id IN (
                        SELECT id FROM order_items WHERE order_id = ?
                    )
                    """)) {
                deleteLinks.setInt(1, order.getId());
                deleteLinks.executeUpdate();
            }

            try (PreparedStatement deleteItems = conn.prepareStatement("DELETE FROM order_items WHERE order_id = ?")) {
                deleteItems.setInt(1, order.getId());
                deleteItems.executeUpdate();
            }

            for (OrderItem item : order.getItems()) {
                double unitPrice = item.getQuantity() == 0 ? 0 : item.getItemPrice() / item.getQuantity();
                String beverageDescription = item.getBeverage().getDescription();

                try (PreparedStatement insertItem = conn.prepareStatement("""
                        INSERT INTO order_items (id, order_id, beverage_id, beverage_description, quantity, note, unit_price, item_price)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                        """)) {
                    insertItem.setInt(1, item.getId());
                    insertItem.setInt(2, order.getId());
                    insertItem.setInt(3, item.getBeverageId());
                    insertItem.setString(4, beverageDescription);
                    insertItem.setInt(5, item.getQuantity());
                    insertItem.setString(6, item.getNote());
                    insertItem.setDouble(7, unitPrice);
                    insertItem.setDouble(8, item.getItemPrice());
                    insertItem.executeUpdate();
                }

                for (String toppingName : extractToppingNames(beverageDescription)) {
                    Integer toppingId = toppingIdsByName.get(toppingName.toLowerCase(Locale.ROOT));
                    if (toppingId == null) {
                        continue;
                    }
                    try (PreparedStatement insertLink = conn.prepareStatement("""
                            INSERT OR IGNORE INTO order_item_toppings (order_item_id, topping_id)
                            VALUES (?, ?)
                            """)) {
                        insertLink.setInt(1, item.getId());
                        insertLink.setInt(2, toppingId);
                        insertLink.executeUpdate();
                    }
                }
            }

            if (previousStatus == null || !previousStatus.equals(order.getStatus())) {
                try (PreparedStatement history = conn.prepareStatement("""
                        INSERT INTO order_status_history (order_id, status, note, changed_at)
                        VALUES (?, ?, ?, ?)
                        """)) {
                    history.setInt(1, order.getId());
                    history.setString(2, order.getStatus());
                    history.setString(3, previousStatus == null ? "Order created" : "State changed");
                    history.setString(4, nowText());
                    history.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException ex) {
            rollbackQuietly();
            throw new RuntimeException("Failed to save order #" + order.getId(), ex);
        } finally {
            resetAutoCommit();
        }
    }

    @Override
    public void savePayment(Payment payment) {
        try (PreparedStatement stmt = conn.prepareStatement("""
                INSERT INTO payments (id, order_id, method, amount, transaction_code, status, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT(id) DO UPDATE SET
                    order_id = excluded.order_id,
                    method = excluded.method,
                    amount = excluded.amount,
                    transaction_code = excluded.transaction_code,
                    status = excluded.status,
                    created_at = excluded.created_at
                """)) {
            stmt.setInt(1, payment.getId());
            stmt.setInt(2, payment.getOrderId());
            stmt.setString(3, payment.getMethod());
            stmt.setDouble(4, payment.getAmount());
            stmt.setString(5, payment.getTransactionCode());
            stmt.setString(6, payment.getStatus());
            stmt.setString(7, nowText());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to save payment for order #" + payment.getOrderId(), ex);
        }
    }

    @Override
    public List<RecipeItem> getRecipeItems(int beverageId) {
        List<RecipeItem> recipeItems = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement("""
                SELECT beverage_id, inventory_item_id, quantity_required
                FROM recipe_items
                WHERE beverage_id = ?
                ORDER BY inventory_item_id
                """)) {
            stmt.setInt(1, beverageId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    recipeItems.add(new RecipeItem(
                            rs.getInt("beverage_id"),
                            rs.getInt("inventory_item_id"),
                            rs.getDouble("quantity_required")
                    ));
                }
            }
            return recipeItems;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to load recipe items for beverage #" + beverageId, ex);
        }
    }

    @Override
    public void saveRecipeItem(RecipeItem item) {
        try (PreparedStatement stmt = conn.prepareStatement("""
                INSERT INTO recipe_items (beverage_id, inventory_item_id, quantity_required)
                VALUES (?, ?, ?)
                ON CONFLICT(beverage_id, inventory_item_id) DO UPDATE SET
                    quantity_required = excluded.quantity_required
                """)) {
            stmt.setInt(1, item.getBeverageId());
            stmt.setInt(2, item.getInventoryItemId());
            stmt.setDouble(3, item.getQuantityRequired());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to save recipe item.", ex);
        }
    }

    @Override
    public void deleteRecipeItem(int beverageId, int inventoryItemId) {
        try (PreparedStatement stmt = conn.prepareStatement("""
                DELETE FROM recipe_items
                WHERE beverage_id = ? AND inventory_item_id = ?
                """)) {
            stmt.setInt(1, beverageId);
            stmt.setInt(2, inventoryItemId);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to delete recipe item.", ex);
        }
    }

    private int nextId(String tableName) {
        String sql = "SELECT COALESCE(MAX(id), 0) + 1 FROM " + tableName;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 1;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to generate next id for " + tableName, ex);
        }
    }

    private String findPersistedOrderStatus(int orderId) {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT status FROM orders WHERE id = ?")) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getString("status") : null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to load current order status.", ex);
        }
    }

    private void loadOrderItems(Order order) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("""
                SELECT id, beverage_id, beverage_description, quantity, note, unit_price, item_price
                FROM order_items
                WHERE order_id = ?
                ORDER BY id
                """)) {
            stmt.setInt(1, order.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int itemId = rs.getInt("id");
                    int beverageId = rs.getInt("beverage_id");
                    String beverageDescription = rs.getString("beverage_description");
                    if (beverageDescription == null || beverageDescription.isBlank()) {
                        beverageDescription = buildLegacyDescription(beverageId, itemId);
                    }
                    int quantity = rs.getInt("quantity");
                    double unitPrice = rs.getDouble("unit_price");
                    if (unitPrice <= 0 && quantity > 0) {
                        unitPrice = rs.getDouble("item_price") / quantity;
                    }

                    Beverage beverage = new PersistedBeverage(beverageDescription, unitPrice);
                    OrderItem item = new OrderItem(
                            itemId,
                            order.getId(),
                            beverageId,
                            beverage,
                            quantity,
                            Optional.ofNullable(rs.getString("note")).orElse("")
                    );
                    order.addRawItem(item);
                }
            }
        }
    }

    private String buildLegacyDescription(int beverageId, int orderItemId) throws SQLException {
        StringBuilder builder = new StringBuilder(findBeverageName(beverageId));
        try (PreparedStatement stmt = conn.prepareStatement("""
                SELECT t.name
                FROM order_item_toppings oit
                JOIN toppings t ON t.id = oit.topping_id
                WHERE oit.order_item_id = ?
                ORDER BY t.id
                """)) {
            stmt.setInt(1, orderItemId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    builder.append(" + ").append(rs.getString("name"));
                }
            }
        }
        return builder.toString();
    }

    private String findBeverageName(int beverageId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT name FROM beverages WHERE id = ?")) {
            stmt.setInt(1, beverageId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        }
        return "Unknown beverage";
    }

    private InventoryItem findInventoryItemById(int inventoryItemId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("""
                SELECT id, name, unit, quantity, reorder_level
                FROM inventory_items
                WHERE id = ?
                """)) {
            stmt.setInt(1, inventoryItemId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new InventoryItem(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("unit"),
                            rs.getDouble("quantity"),
                            rs.getDouble("reorder_level")
                    );
                }
            }
        }
        throw new IllegalArgumentException("Inventory item not found: " + inventoryItemId);
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("role"),
                rs.getInt("active") == 1
        );
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) {
            return LocalDateTime.now();
        }
        try {
            return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException ignored) {
            try {
                return LocalDateTime.parse(value.replace(' ', 'T'));
            } catch (DateTimeParseException ignoredAgain) {
                return LocalDateTime.now();
            }
        }
    }

    private String nowText() {
        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }

    private List<String> extractToppingNames(String beverageDescription) {
        if (beverageDescription == null || beverageDescription.isBlank()) {
            return List.of();
        }

        String[] parts = beverageDescription.split("\\s\\+\\s");
        if (parts.length <= 1) {
            return List.of();
        }

        List<String> names = new ArrayList<>();
        for (int i = 1; i < parts.length; i++) {
            if (!parts[i].isBlank()) {
                names.add(parts[i].trim());
            }
        }
        return names;
    }

    private void rollbackQuietly() {
        try {
            conn.rollback();
        } catch (SQLException ignored) {
        }
    }

    private void resetAutoCommit() {
        try {
            conn.setAutoCommit(true);
        } catch (SQLException ignored) {
        }
    }

    @Override
    public void close() throws SQLException {
        conn.close();
    }

    private static final class PersistedBeverage implements Beverage {
        private final String description;
        private final double unitPrice;

        private PersistedBeverage(String description, double unitPrice) {
            this.description = description == null || description.isBlank() ? "Unknown beverage" : description;
            this.unitPrice = unitPrice;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public double getPrice() {
            return unitPrice;
        }
    }
}
