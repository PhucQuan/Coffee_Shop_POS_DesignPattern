# PROJECT BRIEF

## Product Name

Coffee Shop POS

## Problem

Small coffee shops need a simple tool to create orders, customize drinks, apply promotions, track kitchen status, and process payments. For this school project, the system also needs to clearly demonstrate multiple design patterns in real code.

## Users

- Admin: manages menu, toppings, promotions, users, reports.
- Cashier: creates orders, customizes drinks, applies discounts, accepts payment.
- Kitchen staff: receives orders and updates preparation status.
- Payment gateway: external payment provider simulated through adapters.

## Success criteria

- The app runs on Windows with Java 17.
- The UI supports the core demo flow from login to payment.
- The code clearly maps to the report's pattern explanations.
- All test cases pass.
- The project is easy to explain in a 5-minute demo.

## Demo flow

1. Login as cashier.
2. Add beverage to order.
3. Add toppings and size.
4. Apply discount.
5. Send order to kitchen.
6. Mark order ready.
7. Pay by Momo/VNPay.
8. Login as admin and show report.

## Non-goals

- Real production payment integration.
- Multi-branch inventory management.
- Complex accounting.
- Full authentication security.
