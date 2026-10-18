# Coffee Shop POS - Design Patterns Application

Coffee Shop POS project focused on clean design patterns plus practical store operations (Backend Java API + Frontend ReactJS).

## Architecture & Layers

- `frontend-react`: Modern React POS application (Vite, TailwindCSS, Lucide icons)
- `api`: `WebServer` HTTP server serving endpoints (`/api/health`, `/api/menu`) and static React SPA bundle
- `service`: `AuthService`, `OrderService`, `MenuService`, `PaymentService`, `ReportService`
- `domain`: entities and Design Pattern classes
- `infrastructure`: `DatabaseConnection`, `SqliteRepository`, in-memory fallback, SQL schema support

## Patterns

- Decorator: beverage toppings and size.
- Strategy: discount calculation.
- State: order lifecycle.
- Observer: order status notifications.
- Factory Method: beverage creation by category.
- Singleton: `DatabaseConnection`, `AppConfig`.
- Adapter: Momo/VNPay payment gateways.
- Inventory business logic: deduct ingredients when an order is sent to kitchen and roll back stock when an unpaid prepared order is cancelled.
- Receipt preview: formatted bill shown after successful payment.
- Dashboard: Admin revenue card and top-selling bar chart.
- Receipt image export: save bill preview as PNG for report/demo materials.
- Payment QR simulation: receipt preview and receipt PNG include a VNPay/Momo-style QR block.
- Expanded coffee shop menu: 24 sample beverages across coffee, tea, matcha, and smoothie categories.
- Embedded SQLite persistence: orders, menu, users, toppings, payments, and inventory now persist across app restarts.
- Audit trail: order lifecycle states are stored in `order_status_history`.
- Inventory ledger: every stock deduction/restock is tracked in `inventory_transactions`.
- Order customization persistence: selected toppings are stored in `order_item_toppings`.
- Admin operations console: review order status audit logs and inventory transaction history from the UI.
- SQLite backup: create timestamped database snapshots in the `backups/` folder directly from the admin workspace.
- Recipe editor: admin can map each beverage to its inventory ingredients so stock deduction follows the actual menu.

## Data Layer

- Default database: `pos_data.db` in the project root.
- Runtime repository: `SqliteRepository` is used automatically when the SQLite JDBC driver is available.
- Fallback repository: `InMemoryRepository` is kept for resilience and pattern-focused unit tests.

### Current schema highlights

- Core master data: `users`, `beverages`, `toppings`, `inventory_items`, `settings`
- POS transaction data: `orders`, `order_items`, `payments`
- Rich relational data: `recipe_items`, `order_item_toppings`
- Audit/logging data: `order_status_history`, `inventory_transactions`

## Run

```powershell
.\run.ps1
```

If PowerShell blocks scripts, use:

```bat
run.bat
```

Login accounts:

- `admin` / `123`
- `cashier01` / `123`
- `kitchen01` / `123`

## Test (Unit Testing & Integration Testing)

Hệ thống hỗ trợ thực thi linh hoạt cả hai cấp độ kiểm thử được giảng dạy trong học phần:

### 1. Chạy toàn bộ bài test (18 Unit Tests + 6 Integration Tests):
```powershell
.\test.ps1
# hoặc dùng file batch:
test.bat
```

### 2. Chạy riêng Bộ kiểm thử đơn vị (Unit Testing - 18 Test Cases):
```powershell
.\test.ps1 -Type unit
# hoặc 1-click batch:
test-unit.bat
# hoặc chạy trực tiếp Java class:
java -cp "build\test-classes;libs\*" com.coffeeshop.UnitTestRunner
```

### 3. Chạy riêng Bộ kiểm thử tích hợp (Integration Testing - 6 Test Cases):
```powershell
.\test.ps1 -Type integration
# hoặc 1-click batch:
test-integration.bat
# hoặc chạy trực tiếp Java class:
java -cp "build\test-classes;libs\*" com.coffeeshop.IntegrationTestRunner
```

Toàn bộ 24 Test Cases đều vượt qua với tỷ lệ thành công 100% (18/18 Unit Tests, 6/6 Integration Tests).

## Generate report assets

```bat
generate-assets.bat
```

This creates:

```text
docs/screenshots/receipt-sample.png
```
