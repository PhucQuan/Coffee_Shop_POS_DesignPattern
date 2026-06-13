# Phase 05 - Kitchen/Admin Demo Completion

## Goal

Make the app feel like a complete final-project demo while keeping Design Pattern code clean and easy to explain.

## Completed in this slice

- POS cart now behaves like a real order panel:
  - selected-drink preview
  - add item with toppings
  - increase/decrease quantity
  - remove item
  - cancel order
  - invalid actions disabled by order state
- Kitchen board now reacts through Observer:
  - subscribes to `OrderEventPublisher`
  - refreshes when order status changes
  - shows cards and selected order details
  - receives, completes, and cancels orders through `OrderService`
- Admin view is cleaner and more demo-ready:
  - overview metrics and chart card
  - styled Menu/Topping/Inventory/Order renderers
  - service-backed CRUD forms
  - clearer history/order detail panels
- Asset pipeline is stable:
  - drink/background images live under `src/main/resources/assets`
  - run/test scripts copy resources to `out/production`
- Test coverage now includes Factory Method, Singleton identity, and Adapter failure path.
- Added report-ready docs:
  - `docs/PATTERN_EVIDENCE_TABLE.md`
  - `docs/DEMO_SCRIPT.md`

## Pattern evidence

| Pattern | Demo evidence |
| --- | --- |
| Decorator | POS topping options create decorated beverage price/description |
| Strategy | Discount dropdown changes `DiscountStrategy` used by `OrderService` |
| State | Order buttons enable/disable based on Pending/Preparing/Ready/Paid/Cancelled |
| Observer | Kitchen board refreshes via `OrderEventPublisher` |
| Adapter | Momo/VNPay payment dialogs call `PaymentGateway` adapters |
| Factory Method | Admin/MenuService creates beverages by category through factories |
| Singleton | `DatabaseConnection` and `AppConfig` remain singleton examples |

## Verification

```text
All tests passed: 18/18
```

Command:

```bat
test.bat
```

## Next

- Capture screenshots for Login, Cashier, Kitchen, Admin.
- Update report pattern evidence table with exact class names.
- Prepare 3-5 minute demo script:
  1. Login by role.
  2. Create order with topping.
  3. Apply discount.
  4. Send kitchen and complete.
  5. Pay by Momo/VNPay.
  6. Show Admin report/history/inventory.
