# VERIFY

## Automated verification

Run:

```bat
test.bat
```

Expected:

```text
All tests passed: 18/18
```

## Manual GUI smoke test

Run:

```bat
run.bat
```

Smoke flow:

1. Login as `cashier01` / `123`.
2. Select `Ca phe sua`.
3. Check `Tran chau` and `Size L`.
4. Add to order.
5. Apply 10% discount.
6. Send to kitchen.
7. Open Admin Inventory tab and confirm stock changed after sending to kitchen.
8. Mark ready.
9. Pay by Momo.
10. Confirm payment status changes to processing, then success.
11. Confirm receipt preview opens.
12. In receipt preview, click Save PNG and confirm an image is created.
13. Confirm total and status are displayed.
14. Click New order and confirm a fresh Pending order starts.
15. Use `+ Qty`, `- Qty`, and `Remove` in Cart before sending to kitchen.
16. Confirm disabled buttons are greyed out when the order state does not allow the action.

Admin flow:

1. Login as `admin` / `123`.
2. Open Menu tab.
3. Add, update, and disable a sample menu item.
4. Open Topping tab.
5. Add, update, and disable a sample topping.
6. Open Inventory tab and confirm stock quantities display.
7. Open Revenue report tab and confirm revenue card/top-selling chart display.

Kitchen flow:

1. Login as `kitchen01` / `123`.
2. Refresh active orders.
3. Receive order.
4. Complete order.

## Ship checklist

- Tests pass.
- App opens.
- Demo accounts work.
- Receipt sample can be generated with `generate-assets.bat`.
- Report class names match source code.
- No unfinished TODO in user-facing flow.
- Pattern evidence table is updated after adding or renaming classes.
