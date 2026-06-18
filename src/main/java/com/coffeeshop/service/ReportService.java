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
                .filter(order -> "PAID".equals(order.getStatus()))
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }

    public Map<String, Long> getTopSellingItems() {
        return repository.getOrders().stream()
                .flatMap(order -> order.getItems().stream())
                .collect(Collectors.groupingBy(item -> item.getBeverage().getDescription(), Collectors.summingLong(OrderItem::getQuantity)))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }
}
