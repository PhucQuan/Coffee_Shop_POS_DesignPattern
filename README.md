# Coffee Shop POS - Design Patterns Demo

Java Swing project for the Coffee Shop POS final project.

## Layers

- `presentation`: `LoginView`, `POSView`, `KitchenView`, `AdminView`
- `service`: `AuthService`, `OrderService`, `MenuService`, `PaymentService`, `ReportService`
- `domain`: entities and Design Pattern classes
- `infrastructure`: `DatabaseConnection`, in-memory repository, SQL schema support

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
- Expanded coffee shop menu: 16 sample beverages across coffee, tea, matcha, and smoothie categories.

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
