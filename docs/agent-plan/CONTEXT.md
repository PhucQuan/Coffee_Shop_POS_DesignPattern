# CONTEXT

## Project

Coffee Shop POS is a Java Swing desktop application for a Software Design Patterns final project.

## Goal

Build a presentable POS application that demonstrates practical use of OOP and design patterns:

- Decorator for beverage customization.
- Strategy for discounts.
- State for order lifecycle.
- Observer for order notifications.
- Factory Method for beverage creation.
- Singleton for app/database configuration.
- Adapter for payment gateways.

## Existing implementation

The current project already has:

- Java Swing UI: LoginView, POSView, KitchenView, AdminView.
- Service layer: AuthService, MenuService, OrderService, PaymentService, ReportService.
- Domain models: User, BeverageEntity, Topping, Order, OrderItem, Payment.
- Infrastructure: InMemoryRepository, DatabaseConnection, SQL schema.
- TestRunner covering TC01-TC07.

## Constraints

- Keep Java as the implementation language.
- Keep the four-layer architecture:
  - presentation
  - service
  - domain
  - infrastructure
- Do not put business logic inside Swing views.
- Every pattern must remain easy to identify in code and explain in the report.
- The app must compile with Java 17 using `javac` without Maven/Gradle.
- Prefer simple, stable code over complex framework setup.

## Commands

Run tests:

```bat
test.bat
```

Run app:

```bat
run.bat
```
