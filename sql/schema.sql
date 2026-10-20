CREATE DATABASE IF NOT EXISTS coffee_shop_pos
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE coffee_shop_pos;

CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    role VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS beverages (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    base_price DECIMAL(14,2) NOT NULL,
    category VARCHAR(30) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    image_url VARCHAR(255),
    description VARCHAR(500)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS toppings (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    extra_price DECIMAL(14,2) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS inventory_items (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    unit VARCHAR(20) NOT NULL,
    quantity DECIMAL(14,2) NOT NULL,
    reorder_level DECIMAL(14,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS recipe_items (
    beverage_id INT NOT NULL,
    inventory_item_id INT NOT NULL,
    quantity_required DECIMAL(14,2) NOT NULL,
    PRIMARY KEY (beverage_id, inventory_item_id),
    FOREIGN KEY (beverage_id) REFERENCES beverages(id) ON DELETE CASCADE,
    FOREIGN KEY (inventory_item_id) REFERENCES inventory_items(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS orders (
    id INT PRIMARY KEY,
    created_at DATETIME NOT NULL,
    created_at_label VARCHAR(20),
    order_date DATE,
    status VARCHAR(30) NOT NULL,
    discount_type VARCHAR(50),
    subtotal DECIMAL(14,2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(14,2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(14,2) NOT NULL DEFAULT 0,
    payment_method VARCHAR(30),
    order_type VARCHAR(30),
    inventory_deducted BOOLEAN NOT NULL DEFAULT FALSE,
    INDEX idx_orders_status (status),
    INDEX idx_orders_date (order_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT PRIMARY KEY,
    order_id INT NOT NULL,
    beverage_id INT,
    beverage_description VARCHAR(255),
    image_url VARCHAR(255),
    size VARCHAR(10),
    quantity INT NOT NULL,
    note VARCHAR(500),
    unit_price DECIMAL(14,2) NOT NULL DEFAULT 0,
    item_price DECIMAL(14,2) NOT NULL DEFAULT 0,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (beverage_id) REFERENCES beverages(id),
    INDEX idx_order_items_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS order_item_toppings (
    order_item_id BIGINT NOT NULL,
    topping_id INT NOT NULL,
    PRIMARY KEY (order_item_id, topping_id),
    FOREIGN KEY (order_item_id) REFERENCES order_items(id) ON DELETE CASCADE,
    FOREIGN KEY (topping_id) REFERENCES toppings(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS payments (
    id INT PRIMARY KEY,
    order_id INT NOT NULL UNIQUE,
    method VARCHAR(30) NOT NULL,
    amount DECIMAL(14,2) NOT NULL,
    transaction_code VARCHAR(100),
    status VARCHAR(30) NOT NULL,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Không đặt foreign key tới orders để giữ được lịch sử sau khi đơn bị xóa.
CREATE TABLE IF NOT EXISTS order_status_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    status VARCHAR(30) NOT NULL,
    note VARCHAR(255),
    changed_at DATETIME NOT NULL,
    INDEX idx_history_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS inventory_transactions (
    id BIGINT PRIMARY KEY,
    log_id VARCHAR(64),
    inventory_item_id INT NOT NULL,
    order_id INT,
    change_amount DECIMAL(14,2) NOT NULL DEFAULT 0,
    balance_after DECIMAL(14,2) NOT NULL DEFAULT 0,
    delta_text VARCHAR(50),
    balance_text VARCHAR(50),
    display_time VARCHAR(50),
    reason VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (inventory_item_id) REFERENCES inventory_items(id) ON DELETE CASCADE,
    INDEX idx_inventory_tx_item (inventory_item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS settings (
    `key` VARCHAR(50) PRIMARY KEY,
    value TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Dữ liệu mẫu được frontend gửi qua PUT /api/state trong lần chạy đầu.
-- Nếu bảng web_app_state cũ tồn tại, backend sẽ tự di chuyển dữ liệu rồi xóa bảng đó.
