package com.coffeeshop.tools;

import com.coffeeshop.AppContext;
import com.coffeeshop.domain.model.Order;
import com.coffeeshop.domain.patterns.adapter.MomoAdapter;
import com.coffeeshop.domain.patterns.decorator.Beverage;
import com.coffeeshop.domain.patterns.strategy.PercentDiscountStrategy;
import com.coffeeshop.infrastructure.MenuItemRecord;

import java.io.File;
import java.util.Random;

public class SampleArtifactGenerator {
    public static void main(String[] args) {
        AppContext context = new AppContext();
        Order order = context.orderService.createOrder();

        MenuItemRecord coffee = context.menuService.getActiveMenu().stream()
                .filter(item -> item.getName().equals("Ca phe sua"))
                .findFirst()
                .orElseThrow();
        Beverage beverage = context.menuService.createBeverage(coffee);
        beverage = context.menuService.applyTopping(beverage, "Tran chau");
        beverage = context.menuService.applyTopping(beverage, "Size L");
        context.orderService.addItem(order, coffee.getId(), beverage, 1, "Less sugar");

        MenuItemRecord matcha = context.menuService.getActiveMenu().stream()
                .filter(item -> item.getName().equals("Matcha latte"))
                .findFirst()
                .orElseThrow();
        context.orderService.addItem(order, matcha.getId(), context.menuService.createBeverage(matcha), 1, "");

        context.orderService.setDiscountStrategy(order, new PercentDiscountStrategy(10));
        context.orderService.recalculate(order);
        context.orderService.sendToKitchen(order);
        context.orderService.markReady(order);
        context.paymentService.pay(order, new MomoAdapter(new Random(1)));

        String receipt = context.receiptService.buildReceipt(order);
        File output = new File("docs/screenshots/receipt-sample.png");
        context.receiptImageService.saveReceiptPng(receipt, output);
        System.out.println(output.getAbsolutePath());
    }
}
