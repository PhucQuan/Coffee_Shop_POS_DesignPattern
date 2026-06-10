package com.coffeeshop.service;

import com.coffeeshop.domain.model.Order;
import com.coffeeshop.domain.model.OrderItem;
import com.coffeeshop.domain.model.Payment;

import java.time.format.DateTimeFormatter;

public class ReceiptService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public String buildReceipt(Order order) {
        StringBuilder receipt = new StringBuilder();
        receipt.append(center("COFFEE SHOP POS")).append("\n");
        receipt.append(center("123 Nguyen Hue, Quan 1")).append("\n");
        receipt.append(center("Hotline: 0900 000 000")).append("\n");
        receipt.append("----------------------------------------\n");
        receipt.append("Receipt No : #").append(order.getId()).append("\n");
        receipt.append("Created At : ").append(order.getCreatedAt().format(FORMATTER)).append("\n");
        receipt.append("Status     : ").append(order.getStatus()).append("\n");
        receipt.append("----------------------------------------\n");
        for (OrderItem item : order.getItems()) {
            receipt.append(item.getQuantity()).append(" x ").append(item.getBeverage().getDescription()).append("\n");
            receipt.append(rightMoney(item.getItemPrice())).append("\n");
        }
        receipt.append("----------------------------------------\n");
        receipt.append(lineMoney("Subtotal", order.getSubtotal())).append("\n");
        receipt.append(lineMoney("Discount", order.getDiscountAmount())).append("\n");
        receipt.append(lineMoney("Total", order.getTotalAmount())).append("\n");
        Payment payment = order.getPayment();
        if (payment != null) {
            receipt.append("Method     : ").append(payment.getMethod()).append("\n");
            receipt.append("Txn Code   : ").append(payment.getTransactionCode()).append("\n");
        }
        receipt.append("----------------------------------------\n");
        receipt.append(center("Thank you and see you again!")).append("\n");
        receipt.append(center("[ QR PAYMENT DEMO ]")).append("\n");
        return receipt.toString();
    }

    private String lineMoney(String label, double amount) {
        String money = String.format("%,.0f VND", amount);
        return String.format("%-18s%20s", label + ":", money);
    }

    private String rightMoney(double amount) {
        return String.format("%40s", String.format("%,.0f VND", amount));
    }

    private String center(String text) {
        int width = 40;
        if (text.length() >= width) return text;
        int left = (width - text.length()) / 2;
        return " ".repeat(left) + text;
    }
}
