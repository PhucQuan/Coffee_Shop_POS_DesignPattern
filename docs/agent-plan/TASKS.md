# TASKS FOR AGENTS

## Agent PM

Objective: keep scope aligned with final project requirements.

Inputs:

- `PROJECT_BRIEF.md`
- `PRD.md`
- `STATE.md`

Output:

- Updated scope notes.
- Prioritized backlog.
- Any scope cuts needed for deadline.

Definition of done:

- Backlog has clear phase order.
- Every phase has acceptance criteria.

## Agent Architect

Objective: protect architecture and pattern clarity.

Inputs:

- `ARCHITECTURE.md`
- `src/main/java/com/coffeeshop`

Output:

- Architecture notes.
- Package dependency review.
- Refactor plan if a view contains business logic.

Definition of done:

- No presentation class owns core business rules.
- Pattern packages remain easy to identify.

## Agent Developer - Persistence

Objective: implement Phase 1.

Allowed files:

- `src/main/java/com/coffeeshop/infrastructure/**`
- `src/main/java/com/coffeeshop/service/**`
- `src/test/java/com/coffeeshop/**`

Tasks:

- Introduce repository interfaces where useful.
- Keep current in-memory behavior working.
- Prepare path for SQLite without breaking UI.

Verify:

```bat
test.bat
```

## Agent Developer - Admin CRUD

Objective: implement Phase 2.

Allowed files:

- `src/main/java/com/coffeeshop/presentation/AdminView.java`
- `src/main/java/com/coffeeshop/service/MenuService.java`
- `src/main/java/com/coffeeshop/infrastructure/**`
- `src/test/java/com/coffeeshop/**`

Tasks:

- Add menu CRUD UI controls.
- Add topping CRUD UI controls.
- Validate name and price in MenuService.
- Do not write validation directly in AdminView.

Verify:

```bat
test.bat
```

## Agent Developer - UI Demo Polish

Objective: implement Phase 3.

Allowed files:

- `src/main/java/com/coffeeshop/presentation/**`
- `README.md`

Tasks:

- Improve Swing layout.
- Make POS bill easier to read.
- Add visible status and payment result.
- Add Admin overview/orders/history/users based on Sothirich reference flow. Done.
- Add selected-drink panel, cart quantity controls, remove/cancel order, and disabled invalid POS actions. Done.
- Add observer-driven Kitchen board with order cards and item detail. Done.
- Polish Admin lists/dashboard/cards while preserving service calls. Done.
- Keep actions calling services.

Verify:

```bat
test.bat
run.bat
```

## Agent QA

Objective: protect behavior and demo confidence.

Inputs:

- `PRD.md`
- `src/test/java/com/coffeeshop/TestRunner.java`

Tasks:

- Add missing tests for failure paths.
- Keep TC01-TC07 readable for report.
- Document manual GUI smoke tests.

Verify:

```bat
test.bat
```

## Agent Docs/UML

Objective: align report and Enterprise Architect diagrams with actual code.

Inputs:

- `ARCHITECTURE.md`
- `src/main/java/com/coffeeshop`
- Existing report DOCX

Tasks:

- Produce class diagram package list.
- Produce sequence diagram call flows.
- Update pattern evidence table.
- Update demo script.

Definition of done:

- Every pattern in the report has exact class names from source code.

## Agent Current Slice - Demo Completion

Objective: make the current Swing app presentation-ready without changing architecture.

Allowed files:

- `src/main/java/com/coffeeshop/presentation/**`
- `src/main/resources/**`
- `docs/**`

Tasks:

- Capture manual screenshots for Login, Cashier, Kitchen, Admin.
- Update pattern evidence table with exact classes.
- Add demo script showing Decorator, Strategy, State, Observer, Adapter, Factory, Singleton.
- Keep `test.bat` green after every UI change.

Verify:

```bat
test.bat
run.bat
```
