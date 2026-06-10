# Phase 1 - Persistence and Shared State

## Discuss

The current project uses `InMemoryRepository`. This is acceptable for demonstrating design patterns, but final project quality improves if repository boundaries are clearer and app state is shared across views.

Decision: first introduce repository interfaces and shared app context. Do not jump to SQLite until the current UI flow is stable.

## Plan

1. Extract repository contracts:
   - UserRepository
   - MenuRepository
   - OrderRepository
   - PaymentRepository
2. Make InMemoryRepository implement these contracts or split it into smaller repositories.
3. Ensure AppContext owns one shared repository instance.
4. Add tests for order visibility and payment persistence.

## Execute

Assigned agent: Developer - Persistence.

## Verify

```bat
test.bat
```

Manual:

```bat
run.bat
```

## Ship

Update:

- `STATE.md`
- `ARCHITECTURE.md`
- `README.md` if commands change.
