CREATE TABLE users (
    id INTEGER PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL
);

CREATE TABLE beverages (
    id INTEGER PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    base_price DECIMAL(12, 2) NOT NULL,
    category VARCHAR(30) NOT NULL,
    active BOOLEAN NOT NULL
);

CREATE TABLE toppings (
    id INTEGER PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    extra_price DECIMAL(12, 2) NOT NULL,
    active BOOLEAN NOT NULL
);

CREATE TABLE orders (
    id INTEGER PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    status VARCHAR(30) NOT NULL,
    discount_type VARCHAR(50),
    total_amount DECIMAL(12, 2) NOT NULL
);

CREATE TABLE order_items (
    id INTEGER PRIMARY KEY,
    order_id INTEGER NOT NULL,
    beverage_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    note VARCHAR(255),
    item_price DECIMAL(12, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (beverage_id) REFERENCES beverages(id)
);

CREATE TABLE payments (
    id INTEGER PRIMARY KEY,
    order_id INTEGER NOT NULL,
    method VARCHAR(30) NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    transaction_code VARCHAR(100),
    status VARCHAR(30) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE TABLE inventory_items (
    id INTEGER PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    unit VARCHAR(20) NOT NULL,
    quantity DECIMAL(12, 2) NOT NULL,
    reorder_level DECIMAL(12, 2) NOT NULL
);

CREATE TABLE recipe_items (
    id INTEGER PRIMARY KEY,
    beverage_id INTEGER NOT NULL,
    inventory_item_id INTEGER NOT NULL,
    quantity_required DECIMAL(12, 2) NOT NULL,
    FOREIGN KEY (beverage_id) REFERENCES beverages(id),
    FOREIGN KEY (inventory_item_id) REFERENCES inventory_items(id)
);

INSERT INTO users(id, username, password_hash, role, status) VALUES
(1, 'admin', '123', 'ADMIN', 'ACTIVE'),
(2, 'cashier01', '123', 'CASHIER', 'ACTIVE'),
(3, 'kitchen01', '123', 'KITCHEN', 'ACTIVE');

INSERT INTO beverages(id, name, base_price, category, active) VALUES
(1, 'Ca phe sua', 30000, 'COFFEE', 1),
(2, 'Bac xiu', 32000, 'COFFEE', 1),
(3, 'Tra dao', 35000, 'TEA', 1),
(4, 'Tra sua', 38000, 'TEA', 1),
(5, 'Matcha latte', 42000, 'MATCHA', 1),
(6, 'Sinh to xoai', 45000, 'SMOOTHIE', 1),
(7, 'Espresso', 28000, 'COFFEE', 1),
(8, 'Americano', 30000, 'COFFEE', 1),
(9, 'Latte', 42000, 'COFFEE', 1),
(10, 'Cappuccino', 42000, 'COFFEE', 1),
(11, 'Cold brew', 45000, 'COFFEE', 1),
(12, 'Tra vai', 39000, 'TEA', 1),
(13, 'Tra tac mat ong', 34000, 'TEA', 1),
(14, 'Matcha da xay', 52000, 'MATCHA', 1),
(15, 'Sinh to dau', 48000, 'SMOOTHIE', 1),
(16, 'Cacao nong', 36000, 'COFFEE', 1);

INSERT INTO toppings(id, name, extra_price, active) VALUES
(1, 'Tran chau', 10000, 1),
(2, 'Pudding', 9000, 1),
(3, 'Kem cheese', 12000, 1),
(4, 'Extra shot', 8000, 1),
(5, 'Size L', 7000, 1);

INSERT INTO inventory_items(id, name, unit, quantity, reorder_level) VALUES
(1, 'Coffee beans', 'g', 5000, 500),
(2, 'Fresh milk', 'ml', 10000, 1000),
(3, 'Tea leaves', 'g', 3000, 300),
(4, 'Peach syrup', 'ml', 2500, 300),
(5, 'Matcha powder', 'g', 2000, 200),
(6, 'Mango', 'g', 5000, 500),
(7, 'Pearl', 'g', 4000, 400),
(8, 'Cup L', 'pcs', 300, 30);

INSERT INTO recipe_items(id, beverage_id, inventory_item_id, quantity_required) VALUES
(1, 1, 1, 18),
(2, 2, 1, 15),
(3, 2, 2, 80),
(4, 3, 3, 8),
(5, 3, 4, 40),
(6, 4, 3, 10),
(7, 4, 2, 100),
(8, 5, 5, 12),
(9, 5, 2, 120),
(10, 6, 6, 150),
(11, 7, 1, 18),
(12, 8, 1, 16),
(13, 9, 1, 18),
(14, 9, 2, 120),
(15, 10, 1, 18),
(16, 10, 2, 120),
(17, 11, 1, 22),
(18, 12, 3, 8),
(19, 12, 4, 20),
(20, 13, 3, 8),
(21, 14, 5, 15),
(22, 14, 2, 100),
(23, 15, 6, 120),
(24, 16, 2, 150);
