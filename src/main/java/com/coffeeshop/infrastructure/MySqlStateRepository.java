package com.coffeeshop.infrastructure;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Keeps the existing JSON API contract while persisting each business entity
 * in a dedicated relational table. A legacy web_app_state row is migrated once.
 */
public final class MySqlStateRepository implements AutoCloseable {
    private static final Gson GSON = new Gson();
    private final DatabaseConnection config;

    public MySqlStateRepository(DatabaseConnection config) throws SQLException {
        this.config = config;
        config.ensureDatabaseExists();
        initSchema();
        migrateLegacyState();
    }

    private void initSchema() throws SQLException {
        List<String> ddl = List.of(
                "CREATE TABLE IF NOT EXISTS users (id INT PRIMARY KEY, username VARCHAR(50) NOT NULL UNIQUE, password_hash VARCHAR(255) NOT NULL, full_name VARCHAR(100), role VARCHAR(20) NOT NULL, active BOOLEAN NOT NULL DEFAULT TRUE) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
                "CREATE TABLE IF NOT EXISTS beverages (id INT PRIMARY KEY, name VARCHAR(100) NOT NULL UNIQUE, base_price DECIMAL(14,2) NOT NULL, category VARCHAR(30) NOT NULL, active BOOLEAN NOT NULL DEFAULT TRUE, image_url VARCHAR(255), description VARCHAR(500)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
                "CREATE TABLE IF NOT EXISTS toppings (id INT PRIMARY KEY, name VARCHAR(100) NOT NULL UNIQUE, extra_price DECIMAL(14,2) NOT NULL, active BOOLEAN NOT NULL DEFAULT TRUE) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
                "CREATE TABLE IF NOT EXISTS inventory_items (id INT PRIMARY KEY, name VARCHAR(100) NOT NULL UNIQUE, unit VARCHAR(20) NOT NULL, quantity DECIMAL(14,2) NOT NULL, reorder_level DECIMAL(14,2) NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
                "CREATE TABLE IF NOT EXISTS recipe_items (beverage_id INT NOT NULL, inventory_item_id INT NOT NULL, quantity_required DECIMAL(14,2) NOT NULL, PRIMARY KEY (beverage_id, inventory_item_id), FOREIGN KEY (beverage_id) REFERENCES beverages(id) ON DELETE CASCADE, FOREIGN KEY (inventory_item_id) REFERENCES inventory_items(id) ON DELETE CASCADE) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
                "CREATE TABLE IF NOT EXISTS orders (id INT PRIMARY KEY, created_at DATETIME NOT NULL, created_at_label VARCHAR(20), order_date DATE, status VARCHAR(30) NOT NULL, discount_type VARCHAR(50), subtotal DECIMAL(14,2) NOT NULL DEFAULT 0, discount_amount DECIMAL(14,2) NOT NULL DEFAULT 0, total_amount DECIMAL(14,2) NOT NULL DEFAULT 0, payment_method VARCHAR(30), order_type VARCHAR(30), inventory_deducted BOOLEAN NOT NULL DEFAULT FALSE) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
                "CREATE TABLE IF NOT EXISTS order_items (id BIGINT PRIMARY KEY, order_id INT NOT NULL, beverage_id INT, beverage_description VARCHAR(255), image_url VARCHAR(255), size VARCHAR(10), quantity INT NOT NULL, note VARCHAR(500), unit_price DECIMAL(14,2) NOT NULL DEFAULT 0, item_price DECIMAL(14,2) NOT NULL DEFAULT 0, FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE, FOREIGN KEY (beverage_id) REFERENCES beverages(id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
                "CREATE TABLE IF NOT EXISTS order_item_toppings (order_item_id BIGINT NOT NULL, topping_id INT NOT NULL, PRIMARY KEY (order_item_id, topping_id), FOREIGN KEY (order_item_id) REFERENCES order_items(id) ON DELETE CASCADE, FOREIGN KEY (topping_id) REFERENCES toppings(id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
                "CREATE TABLE IF NOT EXISTS payments (id INT PRIMARY KEY, order_id INT NOT NULL UNIQUE, method VARCHAR(30) NOT NULL, amount DECIMAL(14,2) NOT NULL, transaction_code VARCHAR(100), status VARCHAR(30) NOT NULL, created_at DATETIME NOT NULL, FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
                "CREATE TABLE IF NOT EXISTS order_status_history (id BIGINT PRIMARY KEY AUTO_INCREMENT, order_id INT NOT NULL, status VARCHAR(30) NOT NULL, note VARCHAR(255), changed_at DATETIME NOT NULL, INDEX idx_history_order (order_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
                "CREATE TABLE IF NOT EXISTS inventory_transactions (id BIGINT PRIMARY KEY, log_id VARCHAR(64), inventory_item_id INT NOT NULL, order_id INT, change_amount DECIMAL(14,2) NOT NULL DEFAULT 0, balance_after DECIMAL(14,2) NOT NULL DEFAULT 0, delta_text VARCHAR(50), balance_text VARCHAR(50), display_time VARCHAR(50), reason VARCHAR(255) NOT NULL, created_at DATETIME NOT NULL, FOREIGN KEY (inventory_item_id) REFERENCES inventory_items(id) ON DELETE CASCADE, INDEX idx_inventory_tx_item (inventory_item_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
                "CREATE TABLE IF NOT EXISTS settings (`key` VARCHAR(50) PRIMARY KEY, value TEXT) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
        );
        try (Connection connection = config.openConnection(); Statement statement = connection.createStatement()) {
            for (String sql : ddl) statement.execute(sql);
        }
    }

    private void migrateLegacyState() throws SQLException {
        String payload = null;
        try (Connection connection = config.openConnection()) {
            if (!tableExists(connection, "web_app_state")) return;
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT CAST(payload AS CHAR) FROM web_app_state WHERE state_key='coffee-pos'");
                 ResultSet result = statement.executeQuery()) {
                if (result.next()) payload = result.getString(1);
            }
        }
        if (payload != null && !payload.isBlank()) save(payload);
        try (Connection connection = config.openConnection(); Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE web_app_state");
        }
    }

    private boolean tableExists(Connection connection, String table) throws SQLException {
        try (ResultSet result = connection.getMetaData().getTables(connection.getCatalog(), null, table, new String[]{"TABLE"})) {
            return result.next();
        }
    }

    public Optional<String> load() {
        try (Connection connection = config.openConnection()) {
            if (count(connection, "users") == 0) return Optional.empty();
            JsonObject state = new JsonObject();
            state.add("users", loadUsers(connection));
            state.add("menu", loadMenu(connection));
            state.add("toppings", loadToppings(connection));
            state.add("inventory", loadInventory(connection));
            state.add("recipes", loadRecipes(connection));
            state.add("orders", loadOrders(connection));
            state.add("inventoryLogs", loadInventoryLogs(connection));
            state.addProperty("orderCounter", loadOrderCounter(connection));
            return Optional.of(GSON.toJson(state));
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to load relational data from MySQL.", exception);
        }
    }

    public void save(String json) {
        JsonElement parsed = JsonParser.parseString(json);
        if (!parsed.isJsonObject()) throw new IllegalArgumentException("Application state must be a JSON object.");
        JsonObject state = parsed.getAsJsonObject();

        try (Connection connection = config.openConnection()) {
            connection.setAutoCommit(false);
            try {
                Map<Integer, String> oldStatuses = loadOrderStatuses(connection);
                clearCurrentData(connection);
                Map<String, Integer> beverageIds = insertBeverages(connection, array(state, "menu"));
                Map<String, Integer> toppingIds = insertToppings(connection, array(state, "toppings"));
                Map<String, Integer> inventoryIds = insertInventory(connection, array(state, "inventory"));
                insertUsers(connection, array(state, "users"));
                insertRecipes(connection, array(state, "recipes"), beverageIds, inventoryIds);
                insertOrders(connection, array(state, "orders"), beverageIds, toppingIds, oldStatuses);
                insertInventoryLogs(connection, array(state, "inventoryLogs"), inventoryIds);
                saveSetting(connection, "order_counter", Integer.toString(integer(state, "orderCounter", nextOrderId(state))));
                connection.commit();
            } catch (Exception exception) {
                connection.rollback();
                if (exception instanceof RuntimeException runtime) throw runtime;
                throw new RuntimeException(exception);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to save relational data to MySQL.", exception);
        }
    }

    private void clearCurrentData(Connection connection) throws SQLException {
        List<String> tables = List.of("order_item_toppings", "order_items", "payments", "inventory_transactions",
                "orders", "recipe_items", "beverages", "toppings", "inventory_items", "users");
        try (Statement statement = connection.createStatement()) {
            for (String table : tables) statement.executeUpdate("DELETE FROM `" + table + "`");
        }
    }

    private void insertUsers(Connection connection, JsonArray users) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO users(id,username,password_hash,full_name,role,active) VALUES(?,?,?,?,?,?)")) {
            for (JsonElement element : users) {
                JsonObject user = element.getAsJsonObject();
                statement.setInt(1, integer(user, "id", 0));
                statement.setString(2, text(user, "username", ""));
                statement.setString(3, text(user, "password", ""));
                statement.setString(4, text(user, "fullName", text(user, "username", "")));
                statement.setString(5, text(user, "role", "CASHIER"));
                statement.setBoolean(6, bool(user, "active", true));
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private Map<String, Integer> insertBeverages(Connection connection, JsonArray menu) throws SQLException {
        Map<String, Integer> ids = new HashMap<>();
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO beverages(id,name,base_price,category,active,image_url,description) VALUES(?,?,?,?,?,?,?)")) {
            for (JsonElement element : menu) {
                JsonObject item = element.getAsJsonObject();
                int id = integer(item, "id", 0);
                String name = text(item, "name", "Beverage " + id);
                statement.setInt(1, id);
                statement.setString(2, name);
                statement.setDouble(3, number(item, "price", 0));
                statement.setString(4, text(item, "category", "COFFEE"));
                statement.setBoolean(5, bool(item, "active", true));
                statement.setString(6, text(item, "img", ""));
                statement.setString(7, text(item, "desc", ""));
                statement.addBatch();
                ids.put(normalize(name), id);
            }
            statement.executeBatch();
        }
        return ids;
    }

    private Map<String, Integer> insertToppings(Connection connection, JsonArray toppings) throws SQLException {
        Map<String, Integer> ids = new HashMap<>();
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO toppings(id,name,extra_price,active) VALUES(?,?,?,?)")) {
            for (JsonElement element : toppings) {
                JsonObject item = element.getAsJsonObject();
                int id = integer(item, "id", 0);
                String name = text(item, "name", "Topping " + id);
                statement.setInt(1, id);
                statement.setString(2, name);
                statement.setDouble(3, number(item, "price", 0));
                statement.setBoolean(4, bool(item, "active", true));
                statement.addBatch();
                ids.put(normalize(name), id);
            }
            statement.executeBatch();
        }
        return ids;
    }

    private Map<String, Integer> insertInventory(Connection connection, JsonArray inventory) throws SQLException {
        Map<String, Integer> ids = new HashMap<>();
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO inventory_items(id,name,unit,quantity,reorder_level) VALUES(?,?,?,?,?)")) {
            for (JsonElement element : inventory) {
                JsonObject item = element.getAsJsonObject();
                int id = integer(item, "id", 0);
                String name = text(item, "name", "Inventory " + id);
                statement.setInt(1, id);
                statement.setString(2, name);
                statement.setString(3, text(item, "unit", "unit"));
                statement.setDouble(4, number(item, "quantity", 0));
                statement.setDouble(5, number(item, "min", 0));
                statement.addBatch();
                ids.put(normalize(name), id);
            }
            statement.executeBatch();
        }
        return ids;
    }

    private void insertRecipes(Connection connection, JsonArray recipes, Map<String, Integer> beverages,
                               Map<String, Integer> inventory) throws SQLException {
        Set<String> inserted = new HashSet<>();
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO recipe_items(beverage_id,inventory_item_id,quantity_required) VALUES(?,?,?)")) {
            for (JsonElement element : recipes) {
                JsonObject item = element.getAsJsonObject();
                int beverageId = integer(item, "beverageId", findId(beverages, text(item, "beverageName", ""), -1));
                int inventoryId = integer(item, "inventoryId", findId(inventory, text(item, "inventoryName", ""), -1));
                if (beverageId < 0 || inventoryId < 0 || !inserted.add(beverageId + ":" + inventoryId)) continue;
                statement.setInt(1, beverageId);
                statement.setInt(2, inventoryId);
                statement.setDouble(3, number(item, "quantityRequired", 0));
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private void insertOrders(Connection connection, JsonArray orders, Map<String, Integer> beverages,
                              Map<String, Integer> toppings, Map<Integer, String> oldStatuses) throws SQLException {
        long itemId = 1;
        int paymentId = 1;
        int fallbackBeverage = beverages.values().stream().findFirst().orElse(-1);
        try (PreparedStatement orderSql = connection.prepareStatement("INSERT INTO orders(id,created_at,created_at_label,order_date,status,discount_type,subtotal,discount_amount,total_amount,payment_method,order_type,inventory_deducted) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
             PreparedStatement itemSql = connection.prepareStatement("INSERT INTO order_items(id,order_id,beverage_id,beverage_description,image_url,size,quantity,note,unit_price,item_price) VALUES(?,?,?,?,?,?,?,?,?,?)");
             PreparedStatement toppingSql = connection.prepareStatement("INSERT INTO order_item_toppings(order_item_id,topping_id) VALUES(?,?)");
             PreparedStatement paymentSql = connection.prepareStatement("INSERT INTO payments(id,order_id,method,amount,transaction_code,status,created_at) VALUES(?,?,?,?,?,?,?)");
             PreparedStatement historySql = connection.prepareStatement("INSERT INTO order_status_history(order_id,status,note,changed_at) VALUES(?,?,?,?)")) {
            for (JsonElement element : orders) {
                JsonObject order = element.getAsJsonObject();
                int orderId = integer(order, "id", 0);
                String timeLabel = text(order, "createdAt", "");
                LocalDate orderDate = parseDate(text(order, "orderDate", ""));
                Timestamp createdAt = parseTimestamp(orderDate, timeLabel);
                String status = text(order, "status", "PENDING");
                String method = text(order, "paymentMethod", "CASH");
                double total = number(order, "total", 0);
                orderSql.setInt(1, orderId);
                orderSql.setTimestamp(2, createdAt);
                orderSql.setString(3, timeLabel);
                orderSql.setDate(4, Date.valueOf(orderDate));
                orderSql.setString(5, status);
                orderSql.setString(6, text(order, "discountType", "NONE"));
                orderSql.setDouble(7, number(order, "subtotal", total));
                orderSql.setDouble(8, number(order, "discountAmount", 0));
                orderSql.setDouble(9, total);
                orderSql.setString(10, method);
                orderSql.setString(11, text(order, "orderType", "DINE_IN"));
                orderSql.setBoolean(12, true);
                orderSql.executeUpdate();

                for (JsonElement itemElement : array(order, "items")) {
                    JsonObject item = itemElement.getAsJsonObject();
                    String name = text(item, "name", "Beverage");
                    int beverageId = integer(item, "drinkId", findId(beverages, name, fallbackBeverage));
                    int quantity = integer(item, "qty", 1);
                    double unitPrice = number(item, "unitPrice", number(item, "price", 0));
                    long currentItemId = itemId++;
                    itemSql.setLong(1, currentItemId);
                    itemSql.setInt(2, orderId);
                    if (beverageId < 0) itemSql.setNull(3, java.sql.Types.INTEGER); else itemSql.setInt(3, beverageId);
                    itemSql.setString(4, name);
                    itemSql.setString(5, text(item, "img", ""));
                    itemSql.setString(6, text(item, "size", "M"));
                    itemSql.setInt(7, quantity);
                    itemSql.setString(8, text(item, "note", ""));
                    itemSql.setDouble(9, unitPrice);
                    itemSql.setDouble(10, unitPrice * quantity);
                    itemSql.executeUpdate();
                    for (JsonElement topping : array(item, "toppings")) {
                        String nameValue = topping.isJsonPrimitive() ? topping.getAsString()
                                : text(topping.getAsJsonObject(), "name", "");
                        Integer toppingId = toppings.get(normalize(nameValue));
                        if (toppingId != null) {
                            toppingSql.setLong(1, currentItemId);
                            toppingSql.setInt(2, toppingId);
                            toppingSql.addBatch();
                        }
                    }
                }
                paymentSql.setInt(1, paymentId++);
                paymentSql.setInt(2, orderId);
                paymentSql.setString(3, method);
                paymentSql.setDouble(4, total);
                paymentSql.setString(5, "WEB-" + orderId);
                paymentSql.setString(6, "COMPLETED");
                paymentSql.setTimestamp(7, createdAt);
                paymentSql.addBatch();
                if (!status.equals(oldStatuses.get(orderId))) {
                    historySql.setInt(1, orderId);
                    historySql.setString(2, status);
                    historySql.setString(3, oldStatuses.containsKey(orderId) ? "State changed" : "Order created");
                    historySql.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                    historySql.addBatch();
                }
            }
            toppingSql.executeBatch();
            paymentSql.executeBatch();
            historySql.executeBatch();
        }
    }

    private void insertInventoryLogs(Connection connection, JsonArray logs, Map<String, Integer> inventory) throws SQLException {
        if (inventory.isEmpty()) return;
        int fallback = inventory.values().iterator().next();
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO inventory_transactions(id,log_id,inventory_item_id,order_id,change_amount,balance_after,delta_text,balance_text,display_time,reason,created_at) VALUES(?,?,?,?,?,?,?,?,?,?,?)")) {
            long id = 1;
            for (JsonElement element : logs) {
                JsonObject log = element.getAsJsonObject();
                String delta = text(log, "delta", "0");
                String balance = text(log, "balance", "0");
                statement.setLong(1, id++);
                statement.setString(2, text(log, "id", Long.toString(id)));
                statement.setInt(3, findId(inventory, text(log, "item", ""), fallback));
                Integer orderId = extractOrderId(text(log, "reason", ""));
                if (orderId == null) statement.setNull(4, java.sql.Types.INTEGER); else statement.setInt(4, orderId);
                statement.setDouble(5, numberFromText(delta));
                statement.setDouble(6, numberFromText(balance));
                statement.setString(7, delta);
                statement.setString(8, balance);
                statement.setString(9, text(log, "time", ""));
                statement.setString(10, text(log, "reason", ""));
                statement.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private JsonArray loadUsers(Connection c) throws SQLException {
        JsonArray out = new JsonArray();
        try (Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT id,username,password_hash,full_name,role,active FROM users ORDER BY id")) {
            while (r.next()) {
                JsonObject x = new JsonObject();
                x.addProperty("id", r.getInt("id")); x.addProperty("username", r.getString("username"));
                x.addProperty("password", r.getString("password_hash")); x.addProperty("fullName", r.getString("full_name"));
                x.addProperty("role", r.getString("role")); x.addProperty("active", r.getBoolean("active")); out.add(x);
            }
        }
        return out;
    }

    private JsonArray loadMenu(Connection c) throws SQLException {
        JsonArray out = new JsonArray();
        try (Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT id,name,base_price,category,active,image_url,description FROM beverages ORDER BY id")) {
            while (r.next()) {
                JsonObject x = new JsonObject();
                x.addProperty("id", r.getInt("id")); x.addProperty("name", r.getString("name"));
                x.addProperty("price", r.getDouble("base_price")); x.addProperty("category", r.getString("category"));
                x.addProperty("active", r.getBoolean("active")); x.addProperty("img", empty(r.getString("image_url")));
                x.addProperty("desc", empty(r.getString("description"))); out.add(x);
            }
        }
        return out;
    }

    private JsonArray loadToppings(Connection c) throws SQLException {
        JsonArray out = new JsonArray();
        try (Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT id,name,extra_price,active FROM toppings ORDER BY id")) {
            while (r.next()) {
                JsonObject x = new JsonObject(); x.addProperty("id", r.getInt("id")); x.addProperty("name", r.getString("name"));
                x.addProperty("price", r.getDouble("extra_price")); x.addProperty("active", r.getBoolean("active")); out.add(x);
            }
        }
        return out;
    }

    private JsonArray loadInventory(Connection c) throws SQLException {
        JsonArray out = new JsonArray();
        try (Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT id,name,unit,quantity,reorder_level FROM inventory_items ORDER BY id")) {
            while (r.next()) {
                JsonObject x = new JsonObject(); x.addProperty("id", r.getInt("id")); x.addProperty("name", r.getString("name"));
                x.addProperty("unit", r.getString("unit")); x.addProperty("quantity", r.getDouble("quantity"));
                x.addProperty("min", r.getDouble("reorder_level")); out.add(x);
            }
        }
        return out;
    }

    private JsonArray loadRecipes(Connection c) throws SQLException {
        JsonArray out = new JsonArray();
        String sql = "SELECT r.beverage_id,b.name beverage_name,r.inventory_item_id,i.name inventory_name,r.quantity_required,i.unit FROM recipe_items r JOIN beverages b ON b.id=r.beverage_id JOIN inventory_items i ON i.id=r.inventory_item_id ORDER BY r.beverage_id,r.inventory_item_id";
        try (Statement s = c.createStatement(); ResultSet r = s.executeQuery(sql)) {
            while (r.next()) {
                JsonObject x = new JsonObject(); x.addProperty("beverageId", r.getInt("beverage_id")); x.addProperty("beverageName", r.getString("beverage_name"));
                x.addProperty("inventoryId", r.getInt("inventory_item_id")); x.addProperty("inventoryName", r.getString("inventory_name"));
                x.addProperty("quantityRequired", r.getDouble("quantity_required")); x.addProperty("unit", r.getString("unit")); out.add(x);
            }
        }
        return out;
    }

    private JsonArray loadOrders(Connection c) throws SQLException {
        Map<Integer, JsonArray> byOrder = new LinkedHashMap<>();
        Map<Long, JsonObject> byId = new HashMap<>();
        try (Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT id,order_id,beverage_id,beverage_description,image_url,size,quantity,note,unit_price FROM order_items ORDER BY order_id,id")) {
            while (r.next()) {
                JsonObject x = new JsonObject(); long id = r.getLong("id");
                x.addProperty("cartItemId", "db-" + id); x.addProperty("drinkId", r.getInt("beverage_id"));
                x.addProperty("name", r.getString("beverage_description")); x.addProperty("img", empty(r.getString("image_url")));
                x.addProperty("size", defaultText(r.getString("size"), "M")); x.add("toppings", new JsonArray());
                x.addProperty("note", empty(r.getString("note"))); x.addProperty("unitPrice", r.getDouble("unit_price"));
                x.addProperty("qty", r.getInt("quantity")); byOrder.computeIfAbsent(r.getInt("order_id"), k -> new JsonArray()).add(x); byId.put(id, x);
            }
        }
        try (Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT oit.order_item_id,t.name FROM order_item_toppings oit JOIN toppings t ON t.id=oit.topping_id ORDER BY oit.order_item_id,t.id")) {
            while (r.next()) { JsonObject x = byId.get(r.getLong(1)); if (x != null) x.getAsJsonArray("toppings").add(r.getString(2)); }
        }
        JsonArray out = new JsonArray();
        try (Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT id,created_at,created_at_label,order_date,status,discount_type,subtotal,discount_amount,total_amount,payment_method,order_type FROM orders ORDER BY id DESC")) {
            while (r.next()) {
                JsonObject x = new JsonObject(); int id = r.getInt("id"); Timestamp ts = r.getTimestamp("created_at"); Date date = r.getDate("order_date");
                x.addProperty("id", id); x.addProperty("createdAt", defaultText(r.getString("created_at_label"), ts.toLocalDateTime().toLocalTime().toString()));
                x.addProperty("orderDate", date == null ? ts.toLocalDateTime().toLocalDate().toString() : date.toLocalDate().toString());
                x.add("items", byOrder.getOrDefault(id, new JsonArray())); x.addProperty("subtotal", r.getDouble("subtotal"));
                x.addProperty("discountAmount", r.getDouble("discount_amount")); x.addProperty("discountType", r.getString("discount_type"));
                x.addProperty("total", r.getDouble("total_amount")); x.addProperty("status", r.getString("status"));
                x.addProperty("paymentMethod", r.getString("payment_method")); x.addProperty("orderType", r.getString("order_type")); out.add(x);
            }
        }
        return out;
    }

    private JsonArray loadInventoryLogs(Connection c) throws SQLException {
        JsonArray out = new JsonArray();
        String sql = "SELECT tx.log_id,tx.display_time,i.name item_name,tx.delta_text,tx.balance_text,tx.reason FROM inventory_transactions tx JOIN inventory_items i ON i.id=tx.inventory_item_id ORDER BY tx.id DESC";
        try (Statement s = c.createStatement(); ResultSet r = s.executeQuery(sql)) {
            while (r.next()) {
                JsonObject x = new JsonObject(); x.addProperty("id", r.getString("log_id")); x.addProperty("time", empty(r.getString("display_time")));
                x.addProperty("item", r.getString("item_name")); x.addProperty("delta", empty(r.getString("delta_text")));
                x.addProperty("balance", empty(r.getString("balance_text"))); x.addProperty("reason", r.getString("reason")); out.add(x);
            }
        }
        return out;
    }

    private int loadOrderCounter(Connection c) throws SQLException {
        try (Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT value FROM settings WHERE `key`='order_counter'")) {
            if (r.next()) try { return Integer.parseInt(r.getString(1)); } catch (NumberFormatException ignored) { }
        }
        try (Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT COALESCE(MAX(id),1000)+1 FROM orders")) { return r.next() ? r.getInt(1) : 1001; }
    }

    private Map<Integer, String> loadOrderStatuses(Connection c) throws SQLException {
        Map<Integer, String> out = new HashMap<>();
        try (Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT id,status FROM orders")) { while (r.next()) out.put(r.getInt(1), r.getString(2)); }
        return out;
    }

    private void saveSetting(Connection c, String key, String value) throws SQLException {
        try (PreparedStatement s = c.prepareStatement("INSERT INTO settings(`key`,value) VALUES(?,?) ON DUPLICATE KEY UPDATE value=?")) {
            s.setString(1, key); s.setString(2, value); s.setString(3, value); s.executeUpdate();
        }
    }

    public void clear() {
        try (Connection c = config.openConnection()) {
            c.setAutoCommit(false);
            try (Statement s = c.createStatement()) {
                clearCurrentData(c); s.executeUpdate("DELETE FROM order_status_history"); s.executeUpdate("DELETE FROM settings WHERE `key`='order_counter'"); c.commit();
            } catch (SQLException e) { c.rollback(); throw e; }
        } catch (SQLException e) { throw new RuntimeException("Unable to reset relational data in MySQL.", e); }
    }

    public boolean ping() {
        try (Connection c = config.openConnection(); Statement s = c.createStatement(); ResultSet ignored = s.executeQuery("SELECT 1")) { return true; }
        catch (SQLException e) { return false; }
    }

    private int count(Connection c, String table) throws SQLException {
        try (Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT COUNT(*) FROM `" + table + "`")) { return r.next() ? r.getInt(1) : 0; }
    }

    private static JsonArray array(JsonObject o, String key) { JsonElement e = o.get(key); return e != null && e.isJsonArray() ? e.getAsJsonArray() : new JsonArray(); }
    private static String text(JsonObject o, String key, String fallback) { JsonElement e = o.get(key); return e == null || e.isJsonNull() ? fallback : e.getAsString(); }
    private static int integer(JsonObject o, String key, int fallback) { try { JsonElement e=o.get(key); return e==null||e.isJsonNull()?fallback:e.getAsInt(); } catch(RuntimeException e){return fallback;} }
    private static double number(JsonObject o, String key, double fallback) { try { JsonElement e=o.get(key); return e==null||e.isJsonNull()?fallback:e.getAsDouble(); } catch(RuntimeException e){return fallback;} }
    private static boolean bool(JsonObject o, String key, boolean fallback) { try { JsonElement e=o.get(key); return e==null||e.isJsonNull()?fallback:e.getAsBoolean(); } catch(RuntimeException e){return fallback;} }
    private static String normalize(String value) { return Normalizer.normalize(value==null?"":value, Normalizer.Form.NFD).replaceAll("\\p{M}","").toLowerCase(Locale.ROOT).replace('đ','d').replaceAll("[^a-z0-9]+"," ").trim(); }
    private static int findId(Map<String,Integer> ids, String name, int fallback) { String n=normalize(name); Integer exact=ids.get(n); if(exact!=null)return exact; for(var e:ids.entrySet())if(e.getKey().contains(n)||n.contains(e.getKey()))return e.getValue(); return fallback; }
    private static LocalDate parseDate(String value) { try{return LocalDate.parse(value);}catch(RuntimeException e){return LocalDate.now();} }
    private static Timestamp parseTimestamp(LocalDate date,String value){try{if(value!=null&&value.trim().matches("\\d{1,2}:\\d{2}")){String[] p=value.trim().split(":");return Timestamp.valueOf(LocalDateTime.of(date,LocalTime.of(Integer.parseInt(p[0]),Integer.parseInt(p[1]))));}}catch(RuntimeException ignored){}return Timestamp.valueOf(LocalDateTime.now());}
    private static double numberFromText(String value){try{return Double.parseDouble(value.replace(',','.').replaceAll("[^0-9+\\-.]",""));}catch(RuntimeException e){return 0;}}
    private static Integer extractOrderId(String value){var m=java.util.regex.Pattern.compile("#(\\d+)").matcher(value==null?"":value);return m.find()?Integer.parseInt(m.group(1)):null;}
    private static int nextOrderId(JsonObject state){int max=1000;for(JsonElement e:array(state,"orders"))max=Math.max(max,integer(e.getAsJsonObject(),"id",0));return max+1;}
    private static String empty(String value){return value==null?"":value;}
    private static String defaultText(String value,String fallback){return value==null||value.isBlank()?fallback:value;}

    @Override public void close() { }
}
