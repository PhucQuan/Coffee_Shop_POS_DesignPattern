# ARCHITECTURE

## Layering

```text
presentation
  -> Swing views only: LoginView, POSView, KitchenView, AdminView

service
  -> Use case orchestration: AuthService, MenuService, OrderService, PaymentService, ReportService

domain
  -> Entities and patterns: Order, OrderItem, Beverage, State, Strategy, Decorator, Observer, Factory, Adapter

infrastructure
  -> Repository, database connection, data records
```

## Dependency rule

Presentation can call Service.
Service can call Domain and Infrastructure.
Domain must not depend on Presentation.
Infrastructure must not depend on Presentation.

## Pattern map

| Pattern | Package | Purpose |
|---|---|---|
| Decorator | `domain.patterns.decorator` | Add topping/size behavior without subclass explosion |
| Strategy | `domain.patterns.strategy` | Swap discount calculation at runtime |
| State | `domain.patterns.state` | Enforce order lifecycle rules |
| Observer | `domain.patterns.observer` | Notify cashier/kitchen/report on order status changes |
| Factory Method | `domain.patterns.factory` | Create beverage by category |
| Singleton | `domain.patterns.singleton`, `infrastructure` | Single app config/database connection object |
| Adapter | `domain.patterns.adapter` | Normalize Momo/VNPay payment gateway interface |

## Risk notes

- In-memory repository is simple but not persistent.
- Multiple open app contexts do not share state.
- Swing UI should not grow business rules; move rules into services first.
