# Phase 03 Context - Cashier POS Polish

Date: 2026-06-10

## Goal

Make the Cashier POS screen feel like a real coffee POS instead of a basic Swing demo while preserving the current service-layer and pattern wiring.

## Reference direction

Use the user's reference screenshots as visual inspiration:

- left/center menu area with category cards and drink cards,
- right-side cart/bill panel,
- warm coffee palette,
- large clear add buttons,
- clean spacing.

Do not copy exact artwork, layout, or assets.

## Locked decisions

- Keep Java Swing.
- Keep `OrderService`, `MenuService`, and `PaymentService` as owners of business logic.
- Keep `Decorator` flow for topping/size options.
- Keep `Strategy` flow for discounts.
- Keep `Adapter` flow for payment gateways.
- Do not add external dependencies.
- Do not rewrite Kitchen/Admin in this phase.

## Acceptance criteria

- POS menu uses card-style presentation with drink icons.
- Category selection feels closer to chips/cards than default controls.
- Cart/bill panel is visually clearer and easier to scan.
- Add item still works by button, double-click, and F1.
- Payment shortcuts still work.
- `test.bat` passes.
