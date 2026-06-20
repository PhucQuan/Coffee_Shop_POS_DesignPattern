# Coffee Shop POS - Design Patterns Demo

Java Swing project for the Coffee Shop POS final project.

## Layers

- `presentation`: `LoginView`, `POSView`, `KitchenView`, `AdminView`
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
- Async payment UX: payment runs with `SwingWorker` so the UI stays responsive.
- Dashboard: Admin revenue card and top-selling bar chart.
- Receipt image export: save bill preview as PNG for report/demo materials.
- Payment QR simulation: receipt preview and receipt PNG include a VNPay/Momo-style QR block.
- Expanded coffee shop menu: 24 sample beverages across coffee, tea, matcha, and smoothie categories.
- Embedded SQLite persistence: orders, menu, users, toppings, payments, and inventory now persist across app restarts.
- Audit trail: order lifecycle states are stored in `order_status_history`.
- Inventory ledger: every stock deduction/restock is tracked in `inventory_transactions`.
- Order customization persistence: selected toppings are stored in `order_item_toppings`.

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

## Test

```powershell
.\test.ps1
```

If PowerShell blocks scripts, use:

```bat
test.bat
```

The test runner covers TC01-TC13 without requiring Maven/JUnit.

## Generate report assets

```bat
generate-assets.bat
```

This creates:

```text
docs/screenshots/receipt-sample.png
```
