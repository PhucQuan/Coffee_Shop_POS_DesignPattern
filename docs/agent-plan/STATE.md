# STATE

## Current Status

Baseline Java project exists and tests pass.

Last verified:

```text
All tests passed: 18/18
```

## Done

- Created Java Swing project.
- Implemented seven design patterns.
- Added SQL schema and seed data.
- Added role-based login.
- Added cashier POS view.
- Added kitchen view.
- Added admin view.
- Added test runner for TC01-TC07.
- Completed Admin menu CRUD and topping CRUD through MenuService.
- Added POS New Order/Refresh Menu controls and clearer receipt lines.
- Added Kitchen order-detail panel.
- Added TC08-TC09 for Admin CRUD and MenuService validation.
- Added InventoryItem domain model and InventoryService.
- Added automatic inventory deduction when an order is sent to kitchen.
- Added rollback/restock when an unpaid prepared order is cancelled.
- Added Admin Inventory tab.
- Added TC10-TC11 for inventory deduction/rollback and shortage blocking.
- Added ReceiptService and receipt preview dialog after successful payment.
- Added asynchronous payment flow with SwingWorker and visible payment status.
- Added Admin revenue card and top-selling bar chart.
- Added TC12 for receipt content.
- Added ReceiptImageService and Save PNG button in receipt preview.
- Added SampleArtifactGenerator and `generate-assets.bat` to create report-ready receipt image.
- Added TC13 for receipt PNG export.
- Expanded sample menu to 16 beverages.
- Added VNPay/Momo-style QR simulation in receipt preview and exported receipt PNG.
- Added Admin overview/orders/history/users inspired by Sothirich reference flow.
- Added UserService and TC14 for user add/lock validation.
- Added OpenGSD Core reference plan and product completion plan.
- Completed Phase 01 login polish: branded two-column login, inline validation, role hints, Java2D coffee visual, and Exit action.
- Started Phase 02 app shell: added shared AppShell header/sidebar/logout and applied it to Cashier, Kitchen, and Admin views.
- Completed Phase 03 Cashier POS polish baseline: menu card grid, category toggles, clearer cart summary, grouped order/payment actions.
- Added PaymentDialog with QR simulation, async gateway status, transaction display, and cleaner checkout flow.
- Completed POS cart workflow polish: selected-drink panel, item quantity controls, remove item, cancel order, disabled invalid actions, and scroll-safe cart panel.
- Completed Kitchen board polish: observer-driven refresh, order cards, item detail panel, and state-aware receive/complete/cancel actions.
- Started and verified Admin polish: dashboard cards, styled charts/lists, clearer Menu/Topping/Orders/History/Inventory/Users tabs, and service-backed forms.
- Added reference drink images under `src/main/resources/assets/drinks` and copied resources automatically in run/test scripts.
- Added TC15 for cart quantity workflow.
- Added TC16-TC18 for Factory Method, Singleton identity, and Adapter failure path.

## Known gaps

- Kitchen and POS windows are not synchronized when opened from separate login sessions because current repository is in-memory per app context.
- No persistent SQLite runtime connection yet; SQL script exists, but app uses in-memory repository.
- Swing UI is improving and now usable for demo; Admin/Kitchen can still get more visual polish if time permits.
- No JUnit/Maven project yet; tests use no-dependency TestRunner.
- Receipt preview and PNG export are implemented; PDF export is still not implemented.
- Dashboard chart is simple Java2D, not JFreeChart.

## Next recommended phase

Finish Admin smoke test visually, then prepare report/demo evidence: screenshots, pattern evidence table, and UML sequence/class notes.

## Last updated

2026-06-10
