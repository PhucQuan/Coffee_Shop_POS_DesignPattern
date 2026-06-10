# PRD

## Functional requirements

### Authentication

- User can login with username and password.
- System opens a view based on user role:
  - ADMIN -> AdminView
  - CASHIER -> POSView
  - KITCHEN -> KitchenView

### Menu

- Admin can view beverages and toppings.
- Cashier can select beverages from active menu.
- Cashier can customize beverage with supported decorators.

### Order

- Cashier can create an order.
- Order starts in Pending state.
- Pending order can add/remove items.
- Order can move through Pending -> Preparing -> Ready -> Paid.
- Invalid state transition throws InvalidStateTransitionException.

### Discount

- System supports no discount and percent discount in UI.
- Service supports NoDiscount, PercentDiscount, VIP fixed discount, Buy One Get One.
- Final total must never be negative.

### Kitchen

- Kitchen can view active orders.
- Kitchen can receive order and mark ready.
- When order becomes Ready, cashier observer receives notification.

### Payment

- Cashier can pay by Momo or VNPay adapter.
- Successful payment changes order to Paid.
- Payment stores transaction code.

### Report

- Admin can view total revenue.
- Admin can view top-selling items.

## Quality requirements

- Compile with Java 17.
- No Maven/Gradle required.
- Business logic lives in service/domain, not presentation.
- Each pattern has a dedicated package.
- Tests cover TC01-TC07.

## Acceptance test command

```bat
test.bat
```
