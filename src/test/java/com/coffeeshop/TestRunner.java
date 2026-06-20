package com.coffeeshop;

import com.coffeeshop.domain.model.Order;
import com.coffeeshop.domain.model.InventoryItem;
import com.coffeeshop.domain.model.RecipeItem;
import com.coffeeshop.domain.model.Topping;
import com.coffeeshop.domain.model.User;
import com.coffeeshop.domain.patterns.adapter.MomoAdapter;
import com.coffeeshop.domain.patterns.adapter.PaymentGateway;
import com.coffeeshop.domain.patterns.adapter.PaymentResult;
import com.coffeeshop.domain.patterns.decorator.BaseCoffee;
import com.coffeeshop.domain.patterns.decorator.Beverage;
import com.coffeeshop.domain.patterns.decorator.LargeSizeDecorator;
import com.coffeeshop.domain.patterns.decorator.PearlDecorator;
import com.coffeeshop.domain.patterns.factory.CoffeeFactory;
import com.coffeeshop.domain.patterns.factory.MatchaFactory;
import com.coffeeshop.domain.patterns.factory.TeaFactory;
import com.coffeeshop.domain.patterns.observer.CashierScreen;
import com.coffeeshop.domain.patterns.observer.OrderEventPublisher;
import com.coffeeshop.domain.patterns.singleton.AppConfig;
import com.coffeeshop.domain.patterns.state.InvalidStateTransitionException;
import com.coffeeshop.domain.patterns.strategy.PercentDiscountStrategy;
import com.coffeeshop.infrastructure.DatabaseConnection;
import com.coffeeshop.service.OrderService;
import com.coffeeshop.service.OperationsService;
import com.coffeeshop.service.PaymentService;
import com.coffeeshop.service.MenuService;
import com.coffeeshop.service.InventoryException;
import com.coffeeshop.service.ReceiptService;
import com.coffeeshop.service.ReceiptImageService;
import com.coffeeshop.service.UserService;

import java.io.File;
import java.io.IOException;
import com.coffeeshop.infrastructure.InMemoryRepository;
import com.coffeeshop.infrastructure.InventoryTransactionRecord;
import com.coffeeshop.infrastructure.MenuItemRecord;
import com.coffeeshop.infrastructure.OrderStatusHistoryRecord;
import com.coffeeshop.infrastructure.SqliteRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class TestRunner {
    private int passed = 0;

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        runner.tc01LoginByRole();
        runner.tc02DecoratorPrice();
        runner.tc03StrategyDiscount();
        runner.tc04StateValidFlow();
        runner.tc05InvalidStateTransition();
        runner.tc06MomoSuccessPaid();
        runner.tc07ObserverNotifyCashier();
        runner.tc08AdminMenuCrud();
        runner.tc09MenuValidation();
        runner.tc10InventoryDeductAndRollback();
        runner.tc11InventoryShortageBlocksKitchen();
        runner.tc12ReceiptPreviewContent();
        runner.tc13ReceiptImageExport();
        runner.tc14AdminUserManagement();
        runner.tc15CartQuantityWorkflow();
        runner.tc16FactoryCreatesExpectedBeverages();
        runner.tc17SingletonInstancesAreShared();
        runner.tc18AdapterFailureDoesNotPayOrder();
        runner.tc19SqliteOrderPersistence();
        runner.tc20SqliteAdminCrudPersistence();
        runner.tc21SqliteAuditTrailPersistence();
        runner.tc22SqliteAuditQueriesExposeOperationsData();
        runner.tc23SqliteBackupExportCreatesUsableSnapshot();
        runner.tc24RecipeManagementPersistsToRepository();
        System.out.println("All tests passed: " + runner.passed + "/24");
    }

    private void tc01LoginByRole() {
        AppContext context = new AppContext();
        String role = context.authService.login("cashier01", "123").orElseThrow().getRole();
        assertEquals("CASHIER", role, "TC01 login should return cashier role");
        passed++;
    }

    private void tc02DecoratorPrice() {
        // Decorator Pattern: base beverage is wrapped by PearlDecorator and LargeSizeDecorator.
        Beverage beverage = new LargeSizeDecorator(new PearlDecorator(new BaseCoffee("Ca phe sua", 30000)));
        assertEquals(47000.0, beverage.getPrice(), "TC02 decorator price");
        passed++;
    }

    private void tc03StrategyDiscount() {
        // Strategy Pattern: OrderService receives a PercentDiscountStrategy at runtime.
        InMemoryRepository repo = new InMemoryRepository();
        OrderService orderService = new OrderService(repo, new OrderEventPublisher());
        Order order = orderService.createOrder();
        orderService.addItem(order, 1, new BaseCoffee("Combo", 100000), 1, "");
        orderService.setDiscountStrategy(order, new PercentDiscountStrategy(10));
        orderService.recalculate(order);
        assertEquals(90000.0, order.getTotalAmount(), "TC03 final total after 10% discount");
        passed++;
    }

    private void tc04StateValidFlow() {
        // State Pattern: valid order lifecycle must not throw.
        InMemoryRepository repo = new InMemoryRepository();
        OrderService orderService = new OrderService(repo, new OrderEventPublisher());
        Order order = orderService.createOrder();
        orderService.sendToKitchen(order);
        orderService.markReady(order);
        assertEquals("READY", order.getStatus(), "TC04 state should be READY");
        passed++;
    }

    private void tc05InvalidStateTransition() {
        // State Pattern: PaidState locks all editing/preparing actions.
        InMemoryRepository repo = new InMemoryRepository();
        OrderService orderService = new OrderService(repo, new OrderEventPublisher());
        PaymentService paymentService = new PaymentService(repo);
        Order order = orderService.createOrder();
        orderService.addItem(order, 1, new BaseCoffee("Ca phe sua", 30000), 1, "");
        orderService.sendToKitchen(order);
        orderService.markReady(order);
        paymentService.pay(order, new MomoAdapter(new Random(1)));
        assertThrows(InvalidStateTransitionException.class, () -> order.getState().startPreparing(order), "TC05 paid -> preparing must fail");
        passed++;
    }

    private void tc06MomoSuccessPaid() {
        // Adapter Pattern: PaymentService talks to PaymentGateway, not directly to Momo API.
        InMemoryRepository repo = new InMemoryRepository();
        OrderService orderService = new OrderService(repo, new OrderEventPublisher());
        PaymentService paymentService = new PaymentService(repo);
        Order order = orderService.createOrder();
        orderService.addItem(order, 1, new BaseCoffee("Ca phe sua", 30000), 1, "");
        orderService.sendToKitchen(order);
        orderService.markReady(order);
        paymentService.pay(order, new MomoAdapter(new Random(1)));
        assertEquals("PAID", order.getStatus(), "TC06 order should be paid");
        assertTrue(order.getPayment().getTransactionCode().startsWith("MOMO-"), "TC06 transaction code");
        passed++;
    }

    private void tc07ObserverNotifyCashier() {
        // Observer Pattern: when an order becomes READY, CashierScreen receives notification.
        InMemoryRepository repo = new InMemoryRepository();
        OrderEventPublisher publisher = new OrderEventPublisher();
        CashierScreen cashier = new CashierScreen();
        publisher.subscribe(cashier);
        OrderService orderService = new OrderService(repo, publisher);
        Order order = orderService.createOrder();
        orderService.sendToKitchen(order);
        orderService.markReady(order);
        assertTrue(cashier.getLastMessage().contains("ready"), "TC07 cashier should receive ready notification");
        passed++;
    }

    private void tc08AdminMenuCrud() {
        // Admin CRUD: MenuService owns validation and state changes, AdminView only calls service methods.
        InMemoryRepository repo = new InMemoryRepository();
        MenuService menuService = new MenuService(repo);
        MenuItemRecord item = menuService.addBeverage("Affogato", 39000, "COFFEE");
        assertEquals(25, item.getId(), "TC08 new beverage id");
        menuService.updateBeverage(item, "Cold brew premium", 45000, "COFFEE", true);
        assertEquals("Cold brew premium", item.getName(), "TC08 updated beverage name");
        assertEquals(45000.0, item.getBasePrice(), "TC08 updated beverage price");
        menuService.disableBeverage(item);
        assertTrue(!item.isActive(), "TC08 beverage should be inactive after disable");
        passed++;
    }

    private void tc09MenuValidation() {
        // Validation: invalid admin input must be rejected before it reaches domain data.
        InMemoryRepository repo = new InMemoryRepository();
        MenuService menuService = new MenuService(repo);
        assertThrows(IllegalArgumentException.class, () -> menuService.addBeverage("", 10000, "COFFEE"), "TC09 empty beverage name");
        assertThrows(IllegalArgumentException.class, () -> menuService.addBeverage("Test", -1, "COFFEE"), "TC09 negative beverage price");
        assertThrows(IllegalArgumentException.class, () -> menuService.addBeverage("Test", 10000, "INVALID"), "TC09 invalid category");
        assertThrows(IllegalArgumentException.class, () -> menuService.addTopping("Tran chau", 10000), "TC09 duplicate topping");
        passed++;
    }

    private void tc10InventoryDeductAndRollback() {
        // Business logic: sending to kitchen deducts inventory; cancelling before paid rolls it back.
        InMemoryRepository repo = new InMemoryRepository();
        OrderService orderService = new OrderService(repo, new OrderEventPublisher());
        Order order = orderService.createOrder();
        orderService.addItem(order, 1, new BaseCoffee("Ca phe sua", 30000), 1, "");
        double before = repo.getInventory().stream()
                .filter(item -> item.getName().equals("Coffee beans"))
                .findFirst().orElseThrow().getQuantity();
        orderService.sendToKitchen(order);
        double afterDeduct = repo.getInventory().stream()
                .filter(item -> item.getName().equals("Coffee beans"))
                .findFirst().orElseThrow().getQuantity();
        assertEquals(before - 18, afterDeduct, "TC10 coffee beans should be deducted");
        orderService.cancel(order);
        double afterRollback = repo.getInventory().stream()
                .filter(item -> item.getName().equals("Coffee beans"))
                .findFirst().orElseThrow().getQuantity();
        assertEquals(before, afterRollback, "TC10 coffee beans should be restored after cancel");
        passed++;
    }

    private void tc11InventoryShortageBlocksKitchen() {
        // Inventory guard: an order cannot enter PreparingState when ingredients are not enough.
        InMemoryRepository repo = new InMemoryRepository();
        repo.getInventory().stream()
                .filter(item -> item.getName().equals("Coffee beans"))
                .findFirst().orElseThrow()
                .setQuantity(5);
        OrderService orderService = new OrderService(repo, new OrderEventPublisher());
        Order order = orderService.createOrder();
        orderService.addItem(order, 1, new BaseCoffee("Ca phe sua", 30000), 1, "");
        assertThrows(InventoryException.class, () -> orderService.sendToKitchen(order), "TC11 shortage should block kitchen flow");
        assertEquals("PENDING", order.getStatus(), "TC11 order must remain pending after shortage");
        passed++;
    }

    private void tc12ReceiptPreviewContent() {
        // Receipt simulation: after payment, receipt should contain order, item, total, and transaction code.
        InMemoryRepository repo = new InMemoryRepository();
        OrderService orderService = new OrderService(repo, new OrderEventPublisher());
        PaymentService paymentService = new PaymentService(repo);
        Order order = orderService.createOrder();
        orderService.addItem(order, 1, new BaseCoffee("Ca phe sua", 30000), 1, "");
        orderService.sendToKitchen(order);
        orderService.markReady(order);
        paymentService.pay(order, new MomoAdapter(new Random(1)));
        String receipt = new ReceiptService().buildReceipt(order);
        assertTrue(receipt.contains("COFFEE SHOP POS"), "TC12 receipt should include shop name");
        assertTrue(receipt.contains("Ca phe sua"), "TC12 receipt should include item name");
        assertTrue(receipt.contains("30,000 VND"), "TC12 receipt should include total");
        assertTrue(receipt.contains("MOMO-"), "TC12 receipt should include transaction code");
        passed++;
    }

    private void tc13ReceiptImageExport() {
        // Receipt image export: real report/demo asset can be generated from application data.
        InMemoryRepository repo = new InMemoryRepository();
        OrderService orderService = new OrderService(repo, new OrderEventPublisher());
        PaymentService paymentService = new PaymentService(repo);
        Order order = orderService.createOrder();
        orderService.addItem(order, 1, new BaseCoffee("Ca phe sua", 30000), 1, "");
        orderService.sendToKitchen(order);
        orderService.markReady(order);
        paymentService.pay(order, new MomoAdapter(new Random(1)));
        String receipt = new ReceiptService().buildReceipt(order);
        File output = new File("build/test-receipt.png");
        new ReceiptImageService().saveReceiptPng(receipt, output);
        assertTrue(output.exists() && output.length() > 0, "TC13 receipt PNG should be created");
        passed++;
    }

    private void tc14AdminUserManagement() {
        // Admin back-office: UserService owns user validation and lock/unlock state.
        InMemoryRepository repo = new InMemoryRepository();
        UserService userService = new UserService(repo);
        User user = userService.addUser("cashier02", "123", "CASHIER");
        assertEquals(4, user.getId(), "TC14 new user id");
        assertEquals("CASHIER", user.getRole(), "TC14 user role");
        userService.setActive(user, false);
        assertTrue(!user.isActive(), "TC14 user should be locked");
        assertThrows(IllegalArgumentException.class, () -> userService.addUser("cashier02", "123", "CASHIER"), "TC14 duplicate username");
        passed++;
    }

    private void tc15CartQuantityWorkflow() {
        // POS cart workflow: same drink merges quantity, can be adjusted only while Pending.
        InMemoryRepository repo = new InMemoryRepository();
        OrderService orderService = new OrderService(repo, new OrderEventPublisher());
        Order order = orderService.createOrder();
        orderService.addItem(order, 1, new BaseCoffee("Ca phe sua", 30000), 1, "");
        orderService.addItem(order, 1, new BaseCoffee("Ca phe sua", 30000), 1, "");
        assertEquals(1, order.getItems().size(), "TC15 duplicate item should be merged");
        assertEquals(2, order.getItems().get(0).getQuantity(), "TC15 merged quantity");
        orderService.updateItemQuantity(order, order.getItems().get(0).getId(), 3);
        assertEquals(90000.0, order.getSubtotal(), "TC15 subtotal after quantity update");
        orderService.sendToKitchen(order);
        assertThrows(InvalidStateTransitionException.class,
                () -> orderService.updateItemQuantity(order, order.getItems().get(0).getId(), 1),
                "TC15 update quantity after kitchen should fail");
        passed++;
    }

    private void tc16FactoryCreatesExpectedBeverages() {
        // Factory Method Pattern: each concrete factory hides the concrete beverage class from callers.
        Beverage coffee = new CoffeeFactory().createBeverage("Americano", 30000);
        Beverage tea = new TeaFactory().createBeverage("Tra sua", 38000);
        Beverage matcha = new MatchaFactory().createBeverage("Matcha latte", 42000);
        assertTrue(coffee.getDescription().contains("Americano"), "TC16 coffee factory description");
        assertTrue(tea.getDescription().contains("Tra sua"), "TC16 tea factory description");
        assertTrue(matcha.getDescription().contains("Matcha latte"), "TC16 matcha factory description");
        assertEquals(42000.0, matcha.getPrice(), "TC16 factory price should preserve base price");
        passed++;
    }

    private void tc17SingletonInstancesAreShared() {
        // Singleton Pattern: repeated calls must return the exact same object.
        assertTrue(AppConfig.getInstance() == AppConfig.getInstance(), "TC17 AppConfig singleton identity");
        assertTrue(DatabaseConnection.getInstance() == DatabaseConnection.getInstance(), "TC17 DatabaseConnection singleton identity");
        assertEquals("Coffee Shop POS", AppConfig.getInstance().getAppName(), "TC17 app config value");
        assertTrue(DatabaseConnection.getInstance().getConnectionString().contains("sqlite"), "TC17 connection string");
        passed++;
    }

    private void tc18AdapterFailureDoesNotPayOrder() {
        // Adapter Pattern failure path: PaymentService depends on PaymentGateway and leaves order Ready on failure.
        InMemoryRepository repo = new InMemoryRepository();
        OrderService orderService = new OrderService(repo, new OrderEventPublisher());
        PaymentService paymentService = new PaymentService(repo);
        Order order = orderService.createOrder();
        orderService.addItem(order, 1, new BaseCoffee("Ca phe sua", 30000), 1, "");
        orderService.sendToKitchen(order);
        orderService.markReady(order);
        PaymentResult result = paymentService.pay(order, new FakeFailingGateway());
        assertTrue(!result.isSuccess(), "TC18 fake gateway should fail");
        assertEquals("READY", order.getStatus(), "TC18 failed payment must not mark order paid");
        assertTrue(order.getPayment() == null, "TC18 failed payment must not create payment record");
        passed++;
    }

    private void tc19SqliteOrderPersistence() {
        File dbFile = createTempDatabaseFile();
        try (SqliteRepository repo = new SqliteRepository(dbFile.getAbsolutePath())) {
            OrderService orderService = new OrderService(repo, new OrderEventPublisher());
            PaymentService paymentService = new PaymentService(repo);
            Beverage beverage = new LargeSizeDecorator(new PearlDecorator(new BaseCoffee("Ca phe sua", 30000)));
            Order order = orderService.createOrder();
            orderService.addItem(order, 1, beverage, 2, "less ice");
            orderService.sendToKitchen(order);
            orderService.markReady(order);
            paymentService.pay(order, new MomoAdapter(new Random(1)));

            try (SqliteRepository reloaded = new SqliteRepository(dbFile.getAbsolutePath())) {
                Order persisted = reloaded.getOrders().stream()
                        .filter(candidate -> candidate.getId() == order.getId())
                        .findFirst()
                        .orElseThrow();
                assertEquals("PAID", persisted.getStatus(), "TC19 persisted order status");
                assertEquals(1, persisted.getItems().size(), "TC19 persisted order item count");
                assertTrue(persisted.getItems().get(0).getBeverage().getDescription().contains("Tran chau"),
                        "TC19 persisted topping description");
                assertTrue(persisted.getPayment() != null, "TC19 payment should be reloaded with order");
            }
            passed++;
        } catch (Exception ex) {
            throw new RuntimeException("TC19 sqlite persistence failed", ex);
        }
    }

    private void tc20SqliteAdminCrudPersistence() {
        File dbFile = createTempDatabaseFile();
        try (SqliteRepository repo = new SqliteRepository(dbFile.getAbsolutePath())) {
            MenuService menuService = new MenuService(repo);
            UserService userService = new UserService(repo);

            MenuItemRecord beverage = menuService.addBeverage("Affogato", 39000, "COFFEE");
            menuService.updateBeverage(beverage, "Affogato Premium", 42000, "COFFEE", true);
            Topping topping = menuService.addTopping("Milk foam", 9000);
            menuService.disableTopping(topping);
            User user = userService.addUser("cashier02", "123", "CASHIER");
            userService.setActive(user, false);

            try (SqliteRepository reloaded = new SqliteRepository(dbFile.getAbsolutePath())) {
                MenuItemRecord persistedBeverage = reloaded.getMenu().stream()
                        .filter(item -> item.getId() == beverage.getId())
                        .findFirst()
                        .orElseThrow();
                Topping persistedTopping = reloaded.getToppings().stream()
                        .filter(item -> item.getId() == topping.getId())
                        .findFirst()
                        .orElseThrow();
                User persistedUser = reloaded.getUsers().stream()
                        .filter(item -> item.getId() == user.getId())
                        .findFirst()
                        .orElseThrow();

                assertEquals("Affogato Premium", persistedBeverage.getName(), "TC20 persisted beverage name");
                assertEquals(42000.0, persistedBeverage.getBasePrice(), "TC20 persisted beverage price");
                assertTrue(!persistedTopping.isActive(), "TC20 topping disable should persist");
                assertTrue(!persistedUser.isActive(), "TC20 user lock should persist");
            }
            passed++;
        } catch (Exception ex) {
            throw new RuntimeException("TC20 sqlite admin persistence failed", ex);
        }
    }

    private void tc21SqliteAuditTrailPersistence() {
        File dbFile = createTempDatabaseFile();
        try (SqliteRepository repo = new SqliteRepository(dbFile.getAbsolutePath())) {
            OrderService orderService = new OrderService(repo, new OrderEventPublisher());
            PaymentService paymentService = new PaymentService(repo);
            Order order = orderService.createOrder();
            orderService.addItem(order, 1, new PearlDecorator(new BaseCoffee("Ca phe sua", 30000)), 1, "");

            double before = repo.getInventory().stream()
                    .filter(item -> item.getName().equals("Coffee beans"))
                    .findFirst().orElseThrow().getQuantity();

            orderService.sendToKitchen(order);
            orderService.markReady(order);
            paymentService.pay(order, new MomoAdapter(new Random(1)));

            try (SqliteRepository reloaded = new SqliteRepository(dbFile.getAbsolutePath())) {
                double after = reloaded.getInventory().stream()
                        .filter(item -> item.getName().equals("Coffee beans"))
                        .findFirst().orElseThrow().getQuantity();
                assertTrue(after < before, "TC21 inventory deduction should persist in sqlite");
            }

            try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath())) {
                try (PreparedStatement history = connection.prepareStatement(
                        "SELECT COUNT(*) FROM order_status_history WHERE order_id = ?")) {
                    history.setInt(1, order.getId());
                    try (ResultSet rs = history.executeQuery()) {
                        rs.next();
                        assertTrue(rs.getInt(1) >= 4, "TC21 order history should capture lifecycle states");
                    }
                }
                try (PreparedStatement inventoryTx = connection.prepareStatement(
                        "SELECT COUNT(*) FROM inventory_transactions WHERE order_id = ?")) {
                    inventoryTx.setInt(1, order.getId());
                    try (ResultSet rs = inventoryTx.executeQuery()) {
                        rs.next();
                        assertTrue(rs.getInt(1) >= 1, "TC21 inventory transactions should be logged");
                    }
                }
            }

            passed++;
        } catch (Exception ex) {
            throw new RuntimeException("TC21 sqlite audit persistence failed", ex);
        }
    }

    private void tc22SqliteAuditQueriesExposeOperationsData() {
        File dbFile = createTempDatabaseFile();
        try (SqliteRepository repo = new SqliteRepository(dbFile.getAbsolutePath())) {
            OrderService orderService = new OrderService(repo, new OrderEventPublisher());
            PaymentService paymentService = new PaymentService(repo);
            Order order = orderService.createOrder();
            orderService.addItem(order, 1, new BaseCoffee("Ca phe sua", 30000), 1, "");
            orderService.sendToKitchen(order);
            orderService.markReady(order);
            paymentService.pay(order, new MomoAdapter(new Random(1)));

            OperationsService operationsService = new OperationsService(repo);
            OrderStatusHistoryRecord latestHistory = operationsService.getOrderStatusHistory().stream()
                    .filter(record -> record.getOrderId() == order.getId())
                    .findFirst()
                    .orElseThrow();
            InventoryTransactionRecord inventoryRecord = operationsService.getInventoryTransactions().stream()
                    .filter(record -> order.getId() == (record.getOrderId() == null ? -1 : record.getOrderId()))
                    .findFirst()
                    .orElseThrow();

            assertEquals("PAID", latestHistory.getStatus(), "TC22 latest history should show paid state");
            assertTrue(inventoryRecord.getChangeAmount() < 0, "TC22 inventory transaction should capture deduction");
            passed++;
        } catch (Exception ex) {
            throw new RuntimeException("TC22 operations audit query failed", ex);
        }
    }

    private void tc23SqliteBackupExportCreatesUsableSnapshot() {
        File dbFile = createTempDatabaseFile();
        try (SqliteRepository repo = new SqliteRepository(dbFile.getAbsolutePath())) {
            OrderService orderService = new OrderService(repo, new OrderEventPublisher());
            Order order = orderService.createOrder();
            orderService.addItem(order, 1, new BaseCoffee("Ca phe sua", 30000), 1, "");
            orderService.sendToKitchen(order);

            OperationsService operationsService = new OperationsService(repo);
            Path backupPath = operationsService.backupDatabase();

            assertTrue(Files.exists(backupPath), "TC23 backup file should exist");
            assertTrue(Files.size(backupPath) > 0, "TC23 backup file should not be empty");

            try (SqliteRepository restored = new SqliteRepository(backupPath.toString())) {
                Order persisted = restored.getOrders().stream()
                        .filter(candidate -> candidate.getId() == order.getId())
                        .findFirst()
                        .orElseThrow();
                assertEquals("PREPARING", persisted.getStatus(), "TC23 backup should contain saved order state");
            }

            passed++;
        } catch (Exception ex) {
            throw new RuntimeException("TC23 sqlite backup export failed", ex);
        }
    }

    private void tc24RecipeManagementPersistsToRepository() {
        File dbFile = createTempDatabaseFile();
        try (SqliteRepository repo = new SqliteRepository(dbFile.getAbsolutePath())) {
            MenuService menuService = new MenuService(repo);
            MenuItemRecord beverage = repo.getMenu().stream()
                    .filter(item -> item.getId() == 24)
                    .findFirst()
                    .orElseThrow();
            InventoryItem avocado = repo.getInventory().stream()
                    .filter(item -> item.getName().equals("Avocado"))
                    .findFirst()
                    .orElseThrow();

            menuService.saveRecipeItem(beverage, avocado, 180);

            try (SqliteRepository reloaded = new SqliteRepository(dbFile.getAbsolutePath())) {
                RecipeItem recipeItem = reloaded.getRecipeItems(beverage.getId()).stream()
                        .filter(item -> item.getInventoryItemId() == avocado.getId())
                        .findFirst()
                        .orElseThrow();
                assertEquals(180.0, recipeItem.getQuantityRequired(), "TC24 recipe quantity should persist");

                MenuService reloadedMenuService = new MenuService(reloaded);
                reloadedMenuService.deleteRecipeItem(beverage, avocado);
                assertTrue(reloaded.getRecipeItems(beverage.getId()).stream()
                        .noneMatch(item -> item.getInventoryItemId() == avocado.getId()),
                        "TC24 recipe delete should persist");
            }

            passed++;
        } catch (Exception ex) {
            throw new RuntimeException("TC24 recipe management persistence failed", ex);
        }
    }

    private File createTempDatabaseFile() {
        try {
            File file = File.createTempFile("coffee-shop-pos-test-", ".db");
            file.deleteOnExit();
            return file;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to create temp database file.", ex);
        }
    }

    private void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + ". Expected " + expected + ", actual " + actual);
        }
    }

    private void assertEquals(double expected, double actual, String message) {
        if (Math.abs(expected - actual) > 0.001) {
            throw new AssertionError(message + ". Expected " + expected + ", actual " + actual);
        }
    }

    private void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private void assertThrows(Class<? extends Throwable> expected, Runnable action, String message) {
        try {
            action.run();
        } catch (Throwable throwable) {
            if (expected.isInstance(throwable)) return;
            throw new AssertionError(message + ". Wrong exception: " + throwable);
        }
        throw new AssertionError(message + ". No exception was thrown");
    }

    private static final class FakeFailingGateway implements PaymentGateway {
        public PaymentResult processPayment(double amount) {
            return new PaymentResult(false, "", "Simulated gateway failure");
        }

        public String getGatewayName() {
            return "FakePay";
        }
    }
}
