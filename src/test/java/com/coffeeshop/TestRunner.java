package com.coffeeshop;

import com.coffeeshop.domain.model.Order;
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
import com.coffeeshop.service.PaymentService;
import com.coffeeshop.service.MenuService;
import com.coffeeshop.service.InventoryException;
import com.coffeeshop.service.ReceiptService;
import com.coffeeshop.service.ReceiptImageService;
import com.coffeeshop.service.UserService;

import java.io.File;
import com.coffeeshop.infrastructure.InMemoryRepository;
import com.coffeeshop.infrastructure.MenuItemRecord;

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
        System.out.println("All tests passed: " + runner.passed + "/18");
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
        orderService.setDiscountStrategy(new PercentDiscountStrategy(10));
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
