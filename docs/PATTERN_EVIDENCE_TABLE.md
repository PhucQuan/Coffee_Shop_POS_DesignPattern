# Pattern Evidence Table

Use this table in the final report and demo defense. It maps every required Design Pattern to exact source classes, visible application behavior, and automated tests.

| Pattern | Source classes | Used in feature | Demo steps | Tests |
| --- | --- | --- | --- | --- |
| Decorator | `Beverage`, `BaseCoffee`, `MilkTea`, `Matcha`, `BeverageDecorator`, `PearlDecorator`, `LargeSizeDecorator`, `ExtraShotDecorator`, `MilkDecorator` | Drink customization with toppings/size | In POS, select a drink, tick `Tran chau`/`Size L`, add to cart, verify item description and price | TC02, TC15 |
| Strategy | `DiscountStrategy`, `NoDiscountStrategy`, `PercentDiscountStrategy`, `VipDiscountStrategy`, `BuyOneGetOneStrategy` | Runtime discount selection | In POS, choose discount dropdown, click Apply discount, total changes without changing order code | TC03 |
| State | `OrderState`, `PendingState`, `PreparingState`, `ReadyState`, `PaidState`, `CancelledState`, `InvalidStateTransitionException` | Order lifecycle and disabled invalid actions | Send order to kitchen, mark ready, pay; try invalid edit after sent/paid | TC04, TC05, TC15 |
| Observer | `OrderObserver`, `OrderEventPublisher`, `CashierScreen`, `KitchenScreen`, `ReportLogger`; `KitchenView` subscribes for UI refresh | Notify screens when order status changes | Send/mark ready and show kitchen/cashier notification behavior | TC07 |
| Factory Method | `BeverageFactory`, `CoffeeFactory`, `TeaFactory`, `MatchaFactory`, `SmoothieFactory` | MenuService creates beverage objects by category | In Admin/Menu or POS, drinks are created from category without UI knowing concrete beverage class | TC16 |
| Singleton | `AppConfig`, `DatabaseConnection` | Shared app configuration and database connection demo | Show `getInstance()` returns one shared instance; explain DB runtime is still in-memory for demo | TC17 |
| Adapter | `PaymentGateway`, `PaymentResult`, `MomoAdapter`, `VnpayAdapter` | Payment integration abstraction | Pay by Momo/VNPay from POS; gateway returns normalized `PaymentResult` | TC06, TC18 |

## Service integration map

| Service | Pattern integration |
| --- | --- |
| `OrderService` | Strategy for discounts, State for lifecycle transitions, Observer notifications, inventory deduct/rollback |
| `MenuService` | Factory Method for beverage creation and validation for admin CRUD |
| `PaymentService` | Adapter for Momo/VNPay gateways and State transition to Paid |
| `ReportService` | Reads completed orders for revenue/top-selling reporting |
| `InventoryService` | Deducts stock when order enters kitchen and restores stock on cancel before payment |
| `UserService` | Admin user management, validation, lock/unlock |

## Defense notes

- The project intentionally keeps database runtime in-memory so it is easy to run on any demo machine. `sql/schema.sql` still documents the relational design.
- Swing views call services only; business rules are kept in service/domain classes.
- Tests are no-dependency Java tests through `TestRunner` to avoid Maven/JUnit setup issues during demo.
