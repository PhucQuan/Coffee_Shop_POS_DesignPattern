package com.coffeeshop.presentation;

import com.coffeeshop.AppContext;
import com.coffeeshop.domain.model.Order;
import com.coffeeshop.domain.model.OrderItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class KitchenView extends JFrame {
    private final AppContext context;
    private final DefaultListModel<Order> orderModel = new DefaultListModel<>();
    private final JList<Order> orderList = new JList<>(orderModel);
    private final JTextArea detailArea = new JTextArea();

    public KitchenView(AppContext context) {
        this.context = context;
        setTitle("Coffee Shop POS - Kitchen");
        setSize(900, 560);
        setMinimumSize(new Dimension(820, 520));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JButton refresh = AppTheme.ghostButton("Refresh");
        JButton receive = AppTheme.button("Receive order", AppTheme.WARNING);
        JButton complete = AppTheme.button("Complete order", AppTheme.SUCCESS);
        detailArea.setEditable(false);
        detailArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        detailArea.setBorder(new EmptyBorder(12, 12, 12, 12));
        orderList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showDetails(orderList.getSelectedValue());
            }
        });
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(refresh);
        buttons.add(receive);
        buttons.add(complete);

        JPanel content = AppTheme.card(new BorderLayout(10, 10));
        content.add(AppTheme.section("Active kitchen queue"), BorderLayout.NORTH);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(orderList), new JScrollPane(detailArea));
        split.setDividerLocation(280);
        split.setBorder(null);
        content.add(split, BorderLayout.CENTER);
        content.add(buttons, BorderLayout.SOUTH);
        setContentPane(AppShell.wrap(this, context, "Kitchen",
                "Receive pending orders, prepare drinks, and mark orders ready.",
                content, "Queue", "Preparing", "Ready"));
        refresh.addActionListener(e -> refresh());
        receive.addActionListener(e -> selected(order -> context.orderService.sendToKitchen(order)));
        complete.addActionListener(e -> selected(order -> context.orderService.markReady(order)));
        refresh();
    }

    private void refresh() {
        orderModel.clear();
        context.repository.getOrders().stream()
                .filter(order -> !"PAID".equals(order.getStatus()) && !"CANCELLED".equals(order.getStatus()))
                .forEach(orderModel::addElement);
        detailArea.setText("");
    }

    private void selected(java.util.function.Consumer<Order> action) {
        Order order = orderList.getSelectedValue();
        if (order == null) return;
        try {
            action.accept(order);
            refresh();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void showDetails(Order order) {
        if (order == null) {
            detailArea.setText("");
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("Order #").append(order.getId()).append("\n");
        builder.append("Status: ").append(order.getStatus()).append("\n\n");
        for (OrderItem item : order.getItems()) {
            builder.append(item).append("\n");
        }
        builder.append("\nTotal: ").append(String.format("%,.0f", order.getTotalAmount())).append("d\n");
        detailArea.setText(builder.toString());
    }
}
