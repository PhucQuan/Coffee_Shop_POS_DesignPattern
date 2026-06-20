# Coffee POS UI Checklist

## File anchors

Read these first when relevant:

- `src/main/java/com/coffeeshop/presentation/AppTheme.java`
- `src/main/java/com/coffeeshop/presentation/AppShell.java`
- `src/main/java/com/coffeeshop/presentation/LoginView.java`
- `src/main/java/com/coffeeshop/presentation/POSView.java`
- `src/main/java/com/coffeeshop/presentation/KitchenView.java`
- `src/main/java/com/coffeeshop/presentation/AdminView.java`
- `src/main/java/com/coffeeshop/presentation/PaymentDialog.java`

## Layout checklist

- Start from the dominant workflow, not from decorative containers.
- Keep one primary reading direction per screen.
- Use section headers to break large screens into 2-3 clear zones.
- Give scrolling regions a clear border or card container.
- Avoid making every block visually loud; reserve emphasis for totals, statuses, and primary actions.

## Forms

- Label every input explicitly.
- Put the primary save/add action near the edited data.
- Keep destructive actions visually separate from save/update.
- Validate numeric fields with direct error text or a warning dialog.
- Clear/reset actions should not compete visually with the primary action.

## Lists and renderers

- Show the 2-3 most important facts in each row.
- Use color for state reinforcement, not as the only signal.
- Add padding inside custom renderers so rows do not look cramped.
- Zebra or alternate surfaces are fine when the screen is dense.
- If selecting a row drives a detail pane, make the detail pane readable even with no selection.

## Cards and panels

- Use `AppTheme.card(...)` for elevated white cards.
- Use `AppTheme.roundedPanel(...)` with a border when the block should look contained but flatter.
- Do not combine a visible border with a painted shadow on the same card.
- Keep radius subtle and aligned with the current system.

## Status and actions

- `SUCCESS` for completed/healthy state.
- `WARNING` for in-progress or attention-needed state.
- `DANGER` for cancellation, failure, or destructive actions.
- `ACCENT` for totals, prices, and important highlighted figures.
- Primary actions should be obvious at first glance; everything else should step down visually.

## Role-specific guidance

### Cashier

- Optimize for speed and reduced cognitive load.
- Make totals, order status, and payment state highly visible.
- Keep customization controls close to the selected item.

### Kitchen

- Optimize for queue flow and urgency.
- Surface age/wait-time warnings clearly.
- Keep receive/complete/cancel actions close to the selected order.

### Admin

- Optimize for maintenance, review, and auditability.
- Prefer split views, tabbed management sections, and compact editing forms.
- Show summaries, history, and operational controls without hiding them behind too many clicks.

## Before finishing

- Check that the new UI still feels like part of PurrCoffee.
- Check that text and colors still have enough contrast.
- Check that there is a clear primary action and a clear cancel/back path.
- Check that empty data, invalid input, and long lists are handled intentionally.
