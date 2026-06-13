package com.coffeeshop.presentation;

import com.coffeeshop.AppContext;
import com.coffeeshop.domain.model.Order;
import com.coffeeshop.domain.model.OrderItem;
import com.coffeeshop.domain.patterns.observer.OrderObserver;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.format.DateTimeFormatter;

public class KitchenView extends JFrame implements OrderObserver {
    private final AppContext context;
    private final DefaultListModel<Order> orderModel = new DefaultListModel<>();
    private final JList<Order> orderList = new JList<>(orderModel);
    private final DefaultListModel<String> itemModel = new DefaultListModel<>();
    private final JList<String> itemList = new JList<>(itemModel);
    private final JLabel selectedTitle = new JLabel("Select an order");
    private final JLabel selectedStatus = new JLabel("No order selected");
    private final JLabel selectedTotal = new JLabel(AppTheme.money(0));
    private final JLabel queueCount = new JLabel("0 active");
    private JButton receiveButton;
    private JButton completeButton;
    private JButton cancelButton;

    public KitchenView(AppContext context) {
        this.context = context;
        setTitle("Coffee Shop POS - Kitchen");
        setSize(1120, 680);
        setMinimumSize(new Dimension(980, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(AppShell.wrap(this, context, "Kitchen",
                "Receive pending orders, prepare drinks, and mark orders ready.",
                buildContent(), "Queue", "Preparing", "Ready"));
        context.publisher.subscribe(this);
        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                context.publisher.unsubscribe(KitchenView.this);
            }
        });
        refresh();
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setOpaque(false);
        root.add(queuePanel(), BorderLayout.CENTER);
        root.add(detailPanel(), BorderLayout.EAST);
        return root;
    }

    private JPanel queuePanel() {
        JPanel panel = AppTheme.card(new BorderLayout(12, 12));
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(sectionHeader("Kitchen Queue", "Observer updates this board when POS changes an order."), BorderLayout.WEST);
        queueCount.setForeground(AppTheme.PRIMARY);
        queueCount.setFont(queueCount.getFont().deriveFont(Font.BOLD, 14f));
        top.add(queueCount, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        orderList.setCellRenderer(new KitchenOrderRenderer());
        orderList.setFixedCellHeight(108);
        orderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showDetails(orderList.getSelectedValue());
        });
        JScrollPane scroll = new JScrollPane(orderList);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel detailPanel() {
        JPanel panel = AppTheme.card(new BorderLayout(12, 12));
        panel.setPreferredSize(new Dimension(380, 0));
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        selectedTitle.setFont(selectedTitle.getFont().deriveFont(Font.BOLD, 22f));
        selectedTitle.setForeground(AppTheme.TEXT);
        selectedStatus.setFont(selectedStatus.getFont().deriveFont(Font.BOLD, 13f));
        selectedStatus.setForeground(AppTheme.MUTED);
        selectedTotal.setFont(selectedTotal.getFont().deriveFont(Font.BOLD, 24f));
        selectedTotal.setForeground(AppTheme.ACCENT);
        top.add(selectedTitle);
        top.add(Box.createVerticalStrut(6));
        top.add(selectedStatus);
        top.add(Box.createVerticalStrut(12));
        top.add(selectedTotal);
        panel.add(top, BorderLayout.NORTH);

        itemList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        itemList.setCellRenderer(new ItemRenderer());
        panel.add(new JScrollPane(itemList), BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(2, 2, 8, 8));
        actions.setOpaque(false);
        JButton refresh = AppTheme.ghostButton("Refresh");
        receiveButton = AppTheme.button("Receive", AppTheme.WARNING);
        completeButton = AppTheme.button("Complete", AppTheme.SUCCESS);
        cancelButton = AppTheme.button("Cancel", AppTheme.DANGER);
        actions.add(refresh);
        actions.add(receiveButton);
        actions.add(completeButton);
        actions.add(cancelButton);
        panel.add(actions, BorderLayout.SOUTH);

        refresh.addActionListener(e -> refresh());
        receiveButton.addActionListener(e -> selected(order -> context.orderService.sendToKitchen(order)));
        completeButton.addActionListener(e -> selected(order -> context.orderService.markReady(order)));
        cancelButton.addActionListener(e -> selected(order -> context.orderService.cancel(order)));
        return panel;
    }

    private JPanel sectionHeader(String title, String subtitle) {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 2));
        panel.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(t.getFont().deriveFont(Font.BOLD, 20f));
        t.setForeground(AppTheme.TEXT);
        JLabel s = AppTheme.muted(subtitle);
        panel.add(t);
        panel.add(s);
        return panel;
    }

    private void refresh() {
        Order selected = orderList.getSelectedValue();
        orderModel.clear();
        context.repository.getOrders().stream()
                .filter(order -> !"PAID".equals(order.getStatus()) && !"CANCELLED".equals(order.getStatus()))
                .forEach(orderModel::addElement);
        queueCount.setText(orderModel.getSize() + " active");
        if (selected != null) {
            for (int i = 0; i < orderModel.size(); i++) {
                if (orderModel.get(i).getId() == selected.getId()) {
                    orderList.setSelectedIndex(i);
                    return;
                }
            }
        }
        if (!orderModel.isEmpty()) orderList.setSelectedIndex(0);
        else showDetails(null);
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
        itemModel.clear();
        if (order == null) {
            selectedTitle.setText("Select an order");
            selectedStatus.setText("No order selected");
            selectedTotal.setText(AppTheme.money(0));
            updateButtons(null);
            return;
        }
        selectedTitle.setText("Order #" + order.getId());
        selectedStatus.setText(order.getStatus() + " - " + order.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm")));
        selectedTotal.setText(AppTheme.money(order.getTotalAmount()));
        for (OrderItem item : order.getItems()) {
            itemModel.addElement(item.toString());
        }
        updateButtons(order);
    }

    private void updateButtons(Order order) {
        boolean pending = order != null && "PENDING".equals(order.getStatus());
        boolean preparing = order != null && "PREPARING".equals(order.getStatus());
        boolean cancellable = order != null && !"PAID".equals(order.getStatus()) && !"CANCELLED".equals(order.getStatus());
        if (receiveButton != null) receiveButton.setEnabled(pending);
        if (completeButton != null) completeButton.setEnabled(preparing);
        if (cancelButton != null) cancelButton.setEnabled(cancellable);
    }

    public void onOrderStatusChanged(Order order, String newStatus) {
        SwingUtilities.invokeLater(this::refresh);
    }

    private static final class KitchenOrderRenderer extends JPanel implements ListCellRenderer<Order> {
        private final JLabel id = new JLabel();
        private final JLabel status = new JLabel();
        private final JLabel meta = new JLabel();
        private final JLabel total = new JLabel();

        private KitchenOrderRenderer() {
            setLayout(new BorderLayout(10, 4));
            setBorder(new EmptyBorder(12, 12, 12, 12));
            JPanel text = new JPanel(new GridLayout(3, 1, 0, 2));
            text.setOpaque(false);
            id.setFont(id.getFont().deriveFont(Font.BOLD, 17f));
            status.setFont(status.getFont().deriveFont(Font.BOLD, 12f));
            meta.setForeground(AppTheme.MUTED);
            total.setFont(total.getFont().deriveFont(Font.BOLD, 16f));
            text.add(id);
            text.add(status);
            text.add(meta);
            add(text, BorderLayout.CENTER);
            add(total, BorderLayout.EAST);
        }

        public Component getListCellRendererComponent(JList<? extends Order> list, Order order, int index, boolean selected, boolean focus) {
            id.setText("Order #" + order.getId());
            status.setText(order.getStatus());
            meta.setText(order.getItems().size() + " line(s) - " + order.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm")));
            total.setText(AppTheme.money(order.getTotalAmount()));
            Color statusColor = switch (order.getStatus()) {
                case "PENDING" -> AppTheme.WARNING;
                case "PREPARING" -> AppTheme.SUCCESS;
                case "READY" -> AppTheme.ACCENT;
                default -> AppTheme.MUTED;
            };
            status.setForeground(statusColor);
            setBackground(selected ? new Color(255, 241, 225) : AppTheme.PANEL);
            setOpaque(true);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER),
                    new EmptyBorder(12, 12, 12, 12)));
            return this;
        }
    }

    private static final class ItemRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
            label.setBorder(new EmptyBorder(10, 10, 10, 10));
            label.setBackground(index % 2 == 0 ? AppTheme.SURFACE : Color.WHITE);
            label.setForeground(AppTheme.TEXT);
            return label;
        }
    }
}
