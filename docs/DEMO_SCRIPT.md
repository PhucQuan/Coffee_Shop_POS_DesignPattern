# Coffee Shop POS Demo Script

Target length: 3-5 minutes.

## 1. Login and role routing

1. Run `run.bat`.
2. Login as `cashier01 / 123`.
3. Explain that `AuthService` returns the user role and opens the correct workspace.
4. Mention demo accounts:
   - `admin / 123`
   - `cashier01 / 123`
   - `kitchen01 / 123`

## 2. Cashier POS flow

1. Select `Ca phe sua`.
2. Tick `Tran chau` and `Size L`.
3. Click `Add to cart`.
4. Explain Decorator Pattern:
   - base drink is wrapped by topping/size decorators
   - final price is calculated by wrappers
5. Use `+ Qty` or `- Qty`.
6. Explain that cart changes call `OrderService`, not direct UI logic.

## 3. Discount and order lifecycle

1. Select `10% discount`.
2. Click `Apply discount`.
3. Explain Strategy Pattern:
   - changing discount swaps `DiscountStrategy`
   - `OrderService` recalculates total
4. Click `Send kitchen`.
5. Explain State Pattern:
   - Pending -> Preparing
   - editing is now locked
   - inventory is deducted when entering kitchen

## 4. Kitchen flow

1. Login as `kitchen01 / 123` in another app run, or switch during demo if only one screen is used.
2. Select the active order.
3. Click `Receive order` if still pending, then `Complete order`.
4. Explain Observer Pattern:
   - `OrderEventPublisher` notifies subscribed screens/loggers when order status changes
   - Kitchen board refreshes through observer-style updates

## 5. Payment flow

1. Return to cashier.
2. Click `Mark ready` if needed.
3. Click `Pay Momo` or `Pay VNPay`.
4. Explain Adapter Pattern:
   - POS calls `PaymentGateway`
   - Momo/VNPay have different simulated providers but return one `PaymentResult`
5. Show transaction code and receipt preview.

## 6. Admin back-office

1. Login as `admin / 123`.
2. Show `Overview` metrics/chart.
3. Show `Menu` tab and explain Factory Method:
   - category chooses the concrete factory
   - UI does not instantiate beverage classes directly
4. Show `Inventory` tab and low stock status.
5. Show `Users` tab and role management.
6. Mention Singleton:
   - `AppConfig.getInstance()`
   - `DatabaseConnection.getInstance()`

## Closing line

The project is designed as a layered Java Swing POS demo. The UI is intentionally simple enough to run without external dependencies, while the service/domain layer demonstrates the required Design Patterns with automated tests.
