# OpenGSD Core Reference Applied to Coffee Shop POS

Reference repo cloned locally:

- `work/reference/open-gsd-core`
- GitHub: `open-gsd/gsd-core`
- Package: `@opengsd/gsd-core`
- Version read from `package.json`: `1.3.1-dev.0`

This file records how the Coffee Shop POS project should use the OpenGSD approach. It is not a copy of GSD internals. It adapts the method to this Java Design Patterns final project.

## Key OpenGSD idea

OpenGSD uses a phase loop:

```text
Discuss -> UI design -> Plan -> Execute -> Verify -> Ship
```

For this POS project, that means each visible product improvement should be handled as a bounded phase with:

- decisions first,
- UI contract before code when the change is visual,
- file-level implementation plan,
- verification by `test.bat` and manual GUI smoke test,
- docs/report update after the phase is done.

## Recommended planning artifact shape

OpenGSD's canonical planning memory is `.planning/`. Our project currently has `docs/agent-plan/`. To avoid churn, keep `docs/agent-plan/` for now, but structure new work using the same concepts:

| OpenGSD artifact | Current project equivalent | Purpose |
| --- | --- | --- |
| `PROJECT.md` | `docs/agent-plan/PROJECT_BRIEF.md` | Project identity and value |
| `ROADMAP.md` | `docs/PRODUCT_COMPLETION_PLAN.md` | Phase order |
| `REQUIREMENTS.md` | Future `docs/agent-plan/REQUIREMENTS.md` | Checkable acceptance criteria |
| `STATE.md` | `docs/agent-plan/STATE.md` | Current position |
| `BACKLOG.md` | `docs/agent-plan/BACKLOG.md` | Deferred work |
| phase `CONTEXT.md` | Future `docs/agent-plan/phases/<NN>-*/CONTEXT.md` | Decisions before planning |
| phase `UI-SPEC.md` | Future per-phase UI spec | Layout and interaction contract |
| phase `PLAN.md` | Future per-phase implementation plan | Files, tasks, verification |
| phase `VERIFICATION.md` | Future per-phase verification report | What passed, what remains |

## Current milestone

Milestone: `v1.0 Final Project Demo`

Goal: turn the Java Swing Coffee Shop POS from a working pattern demo into a coherent final-project app with professional-enough UX and strong pattern evidence.

## Phase roadmap

### Phase 01 - Login and App Shell

Goal: replace the basic login with a polished branded entry screen and consistent navigation shell.

OpenGSD loop:

- Discuss: choose visual direction, roles, and login behavior.
- UI design: define login layout, shell header/sidebar, validation states.
- Plan: split into LoginView, AppTheme, shared shell helpers, tests.
- Execute: implement without moving auth logic into UI.
- Verify: TC01 login, manual role routing, no UI overlap.
- Ship: update screenshots and report section.

Status: next recommended phase.

### Phase 02 - Cashier POS Polish

Goal: make POS screen look like the primary production workflow.

Scope:

- menu cards,
- cart rows,
- topping/options panel,
- payment dialog,
- QR/receipt flow.

Primary patterns demonstrated:

- Decorator,
- Strategy,
- Adapter,
- State.

### Phase 03 - Kitchen Board

Goal: make kitchen state transitions visible as a queue board.

Scope:

- Pending/Preparing/Ready columns,
- order cards,
- age warning,
- receive/complete actions.

Primary patterns demonstrated:

- State,
- Observer,
- Inventory business rule.

### Phase 04 - Admin Back Office Polish

Goal: refine the admin area into a coherent management dashboard.

Scope:

- overview cards,
- order history,
- menu/topping CRUD,
- inventory low-stock highlighting,
- users.

Primary patterns demonstrated:

- Factory Method,
- Singleton,
- service-layer validation.

### Phase 05 - Report, UML, and Demo Evidence

Goal: align report, UML, screenshots, and code.

Scope:

- class diagrams from actual packages,
- sequence diagrams from actual service calls,
- pattern evidence table,
- demo script,
- screenshots.

## Phase 01 draft context

Locked decisions for Phase 01:

- Keep Java Swing.
- Keep `AuthService` as the only login business logic owner.
- Use `AppTheme` for shared colors/fonts/buttons.
- Login must route by role: ADMIN, CASHIER, KITCHEN.
- Do not add database dependency in this phase.
- Do not copy assets from reference repos.

Deferred:

- Real persistent sessions.
- Password hashing beyond demo scope.
- JavaFX migration.

## Phase 01 draft UI spec

Login screen:

- Window size: about `960x600`.
- Left panel:
  - brand name,
  - short tagline,
  - demo account hints,
  - simple coffee visual drawn in Java2D or existing `DrinkIconFactory`.
- Right panel:
  - username field,
  - password field,
  - sign-in button,
  - error message area,
  - exit button.

App shell:

- Header:
  - app name,
  - role,
  - current date/time,
  - logout.
- Sidebar:
  - role-specific navigation labels.
- Content:
  - existing role view embedded or styled consistently.

## Phase 01 acceptance criteria

- `test.bat` passes.
- Login validates empty input and bad credentials with in-form message.
- Correct credentials route to correct role view.
- Login UI no longer looks like default Swing form.
- No business logic added to `LoginView` beyond event wiring.

## Verification commands

```bat
test.bat
run.bat
```

Manual smoke test:

1. Open app.
2. Try empty login.
3. Try wrong password.
4. Login `admin / 123`.
5. Logout or close.
6. Login `cashier01 / 123`.
7. Login `kitchen01 / 123`.

## How this differs from earlier local GSD install

Earlier project docs were based on the local `.codex/gsd-core` install and hand-written `docs/agent-plan` files. The `open-gsd/gsd-core` repo confirms the stricter loop and artifact vocabulary:

- use phases, not a loose task list;
- add UI spec before visual implementation;
- verify before calling a phase done;
- keep state and decisions small but explicit.

Going forward, new substantial work should follow this file and `PRODUCT_COMPLETION_PLAN.md`.
