package com.coffeeshop.service;

import com.coffeeshop.domain.model.Order;
import com.coffeeshop.domain.model.OrderItem;
import com.coffeeshop.infrastructure.Repository;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportService {
    private final Repository repository;

    public ReportService(Repository repository) {
        this.repository = repository;
    }

    public double getRevenue() {
        return repository.getOrders().stream()
                .filter(this::isCompletedSale)
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }

    public Map<String, Long> getTopSellingItems() {
        return repository.getOrders().stream()
                .filter(this::isCompletedSale)
                .flatMap(order -> order.getItems().stream())
                .collect(Collectors.groupingBy(item -> item.getBeverage().getDescription(), Collectors.summingLong(OrderItem::getQuantity)))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    public Map<String, Long> getMenuSales() {
        return repository.getOrders().stream()
                .filter(this::isCompletedSale)
                .flatMap(order -> order.getItems().stream())
                .collect(Collectors.groupingBy(item -> baseMenuName(item.getBeverage().getDescription()), Collectors.summingLong(OrderItem::getQuantity)))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    public Map<String, Long> getToppingSales() {
        Map<String, Long> totals = new LinkedHashMap<>();
        repository.getOrders().stream()
                .filter(this::isCompletedSale)
                .flatMap(order -> order.getItems().stream())
                .forEach(item -> {
                    String[] parts = item.getBeverage().getDescription().split("\\s+\\+\\s+");
                    for (int i = 1; i < parts.length; i++) {
                        totals.merge(parts[i].trim(), (long) item.getQuantity(), Long::sum);
                    }
                });
        return totals.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    private String baseMenuName(String description) {
        String[] parts = description.split("\\s+\\+\\s+", 2);
        return parts[0].trim();
    }

    private boolean isCompletedSale(Order order) {
        return order.getPayment() != null
                && "SUCCESS".equals(order.getPayment().getStatus())
                && !"CANCELLED".equals(order.getStatus());
    }
}
