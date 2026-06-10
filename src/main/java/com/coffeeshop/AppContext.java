package com.coffeeshop;

import com.coffeeshop.domain.patterns.observer.CashierScreen;
import com.coffeeshop.domain.patterns.observer.KitchenScreen;
import com.coffeeshop.domain.patterns.observer.OrderEventPublisher;
import com.coffeeshop.domain.patterns.observer.ReportLogger;
import com.coffeeshop.infrastructure.InMemoryRepository;
import com.coffeeshop.service.*;

public class AppContext {
    public final InMemoryRepository repository = new InMemoryRepository();
    public final OrderEventPublisher publisher = new OrderEventPublisher();
    public final CashierScreen cashierObserver = new CashierScreen();
    public final KitchenScreen kitchenObserver = new KitchenScreen();
    public final ReportLogger reportLogger = new ReportLogger();
    public final AuthService authService = new AuthService(repository);
    public final UserService userService = new UserService(repository);
    public final MenuService menuService = new MenuService(repository);
    public final InventoryService inventoryService = new InventoryService(repository);
    public final OrderService orderService = new OrderService(repository, publisher, inventoryService);
    public final PaymentService paymentService = new PaymentService(repository);
    public final ReportService reportService = new ReportService(repository);
    public final ReceiptService receiptService = new ReceiptService();
    public final ReceiptImageService receiptImageService = new ReceiptImageService();

    public AppContext() {
        publisher.subscribe(cashierObserver);
        publisher.subscribe(kitchenObserver);
        publisher.subscribe(reportLogger);
    }
}
