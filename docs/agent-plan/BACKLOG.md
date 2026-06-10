# BACKLOG

## Phase 1 - Persistence and shared state

Goal: make the app feel more real by replacing or wrapping in-memory storage with a reusable shared repository/persistence layer.

Tasks:

- Add repository interfaces.
- Add SQLiteRepository or SharedInMemoryRepository.
- Ensure POSView and KitchenView can see the same orders in one app session.
- Keep `test.bat` passing.

## Phase 2 - Admin CRUD

Goal: complete AdminView for menu/topping management.

Status: completed for in-memory demo scope.

Tasks:

- Add create/update/disable beverage form. Done.
- Add create/update/disable topping form. Done.
- Keep business validation in MenuService. Done.
- Add tests for menu validation. Done.

## Phase 3 - UI polish and demo readiness

Goal: make the Swing UI more presentable for final demo.

Tasks:

- Improve layout spacing and labels. Done.
- Add order status labels. Done.
- Add receipt panel. Done.
- Add Admin overview/orders/history/users inspired by Sothirich reference flow. Done.
- Add clear demo data reset.
- Add screenshots for report.

## Phase 4 - Testing upgrade

Goal: make testing more formal.

Tasks:

- Option A: keep TestRunner but improve output.
- Option B: add Maven + JUnit 5 if dependency installation is allowed.
- Add tests for Factory, Singleton, Adapter failure path.

## Phase 5 - Report/code alignment

Goal: make code and report perfectly aligned.

Tasks:

- Update class diagram from actual package structure.
- Update sequence diagrams based on service calls.
- Add pattern evidence table with exact class names.
- Add demo script with screenshots.
- Add Sothirich reference decision note. Done.
