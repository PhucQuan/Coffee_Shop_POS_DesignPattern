package com.coffeeshop;

import com.coffeeshop.domain.patterns.observer.CashierScreen;
import com.coffeeshop.domain.patterns.observer.KitchenScreen;
import com.coffeeshop.domain.patterns.observer.OrderEventPublisher;
import com.coffeeshop.domain.patterns.observer.ReportLogger;
import com.coffeeshop.infrastructure.DatabaseConnection;
import com.coffeeshop.infrastructure.InMemoryRepository;
import com.coffeeshop.infrastructure.Repository;
import com.coffeeshop.infrastructure.SqliteRepository;
import com.coffeeshop.service.AuthService;
import com.coffeeshop.service.InventoryService;
import com.coffeeshop.service.MenuService;
import com.coffeeshop.service.OrderService;
import com.coffeeshop.service.OperationsService;
import com.coffeeshop.service.PaymentService;
import com.coffeeshop.service.ReceiptImageService;
import com.coffeeshop.service.ReceiptService;
import com.coffeeshop.service.ReportService;
import com.coffeeshop.service.UserService;

public class AppContext {
    public final Repository repository;
    public final OrderEventPublisher publisher = new OrderEventPublisher();
    public final CashierScreen cashierObserver = new CashierScreen();
    public final KitchenScreen kitchenObserver = new KitchenScreen();
    public final ReportLogger reportLogger = new ReportLogger();
    public final AuthService authService;
    public final UserService userService;
    public final MenuService menuService;
    public final InventoryService inventoryService;
    public final OrderService orderService;
    public final PaymentService paymentService;
    public final ReportService reportService;
    public final OperationsService operationsService;
    public final ReceiptService receiptService = new ReceiptService();
    public final ReceiptImageService receiptImageService = new ReceiptImageService();

    public AppContext() {
        Repository repo;
        try {
            Class.forName("org.sqlite.JDBC");
            repo = new SqliteRepository(DatabaseConnection.getInstance().getDatabasePath());
            System.out.println("Loaded SQLite Repository.");
        } catch (Exception e) {
            System.out.println("SQLite not available, falling back to InMemoryRepository.");
            repo = new InMemoryRepository();
        }
        this.repository = repo;
        
        this.authService = new AuthService(repository);
        this.userService = new UserService(repository);
        this.menuService = new MenuService(repository);
        this.inventoryService = new InventoryService(repository);
        this.orderService = new OrderService(repository, publisher, inventoryService);
        this.paymentService = new PaymentService(repository);
        this.reportService = new ReportService(repository);
        this.operationsService = new OperationsService(repository);
        publisher.subscribe(cashierObserver);
        publisher.subscribe(kitchenObserver);
        publisher.subscribe(reportLogger);
    }
}
