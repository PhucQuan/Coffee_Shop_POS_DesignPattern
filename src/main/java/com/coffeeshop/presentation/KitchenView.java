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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class KitchenView extends JFrame implements OrderObserver {
    private final AppContext context;
    private final DefaultListModel<Order> preparingModel = new DefaultListModel<>();
    private final DefaultListModel<Order> readyModel = new DefaultListModel<>();
    private final JList<Order> preparingList = new JList<>(preparingModel);
    private final JList<Order> readyList = new JList<>(readyModel);
    private final DefaultListModel<String> itemModel = new DefaultListModel<>();
    private final JList<String> itemList = new JList<>(itemModel);
    private final JLabel selectedTitle = new JLabel("Select an order");
    private final JLabel selectedStatus = new JLabel("No order selected");
    private final JLabel selectedTotal = new JLabel(AppTheme.money(0));
    private final JLabel queueCount = new JLabel("0 active");
    private Order currentSelectedOrder;
    private JButton completeButton;
    private JButton deliverButton;

    public KitchenView(AppContext context) {
        this.context = context;
        setTitle("Coffee Shop POS - Kitchen");
        setSize(1280, 720);
        setMinimumSize(new Dimension(1024, 640));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(AppShell.wrap(this, context, "Kitchen",
                "Kanban Board: Complete prepared drinks and mark orders ready.",
                buildContent(), nav -> {}, "Kanban"));
        context.publisher.subscribe(this);
        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                context.publisher.unsubscribe(KitchenView.this);
            }
        });
        Timer timer = new Timer(30000, e -> refresh());
        timer.start();
        refresh();
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout(24, 24));
        root.setOpaque(false);
        root.add(queuePanel(), BorderLayout.CENTER);
        root.add(detailPanel(), BorderLayout.EAST);
        return root;
    }

    private JPanel queuePanel() {
        JPanel panel = AppTheme.card(new BorderLayout(24, 24));
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(sectionHeader("Kitchen Kanban", "Orders > 15 mins will show a warning."), BorderLayout.WEST);
        queueCount.setForeground(AppTheme.PRIMARY);
        queueCount.setFont(queueCount.getFont().deriveFont(Font.BOLD, 14f));
        top.add(queueCount, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        JPanel kanban = new JPanel(new GridLayout(1, 2, 16, 0));
        kanban.setOpaque(false);
        kanban.add(createColumn("Preparing", preparingList));
        kanban.add(createColumn("Ready", readyList));
        panel.add(kanban, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createColumn(String title, JList<Order> list) {
        JPanel col = new JPanel(new BorderLayout(0, 8));
        col.setOpaque(false);
        JLabel label = new JLabel(title);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
        label.setForeground(AppTheme.TEXT);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        col.add(label, BorderLayout.NORTH);

        list.setCellRenderer(new KitchenOrderRenderer());
        list.setFixedCellHeight(114);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && list.getSelectedValue() != null) {
                if (list != preparingList) preparingList.clearSelection();
                if (list != readyList) readyList.clearSelection();
                currentSelectedOrder = list.getSelectedValue();
                showDetails(currentSelectedOrder);
            }
        });
        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        col.add(scroll, BorderLayout.CENTER);
        return col;
    }

    private JPanel detailPanel() {
        JPanel panel = AppTheme.card(new BorderLayout(24, 24));
        panel.setPreferredSize(new Dimension(340, 0));
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

        JPanel actions = new JPanel(new GridLayout(1, 3, 8, 8));
        actions.setOpaque(false);
        JButton refreshBtn = AppTheme.ghostButton("Refresh");
        completeButton = AppTheme.button("Complete", AppTheme.SUCCESS);
        deliverButton = AppTheme.button("Deliver", AppTheme.PRIMARY);
        actions.add(refreshBtn);
        actions.add(completeButton);
        actions.add(deliverButton);
        panel.add(actions, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> refresh());
        completeButton.addActionListener(e -> executeAction(order -> context.orderService.markReady(order)));
        deliverButton.addActionListener(e -> executeAction(order -> {
            order.getState().pay(order);
            context.repository.saveOrder(order);
            context.publisher.notifyObservers(order, order.getStatus());
        }));
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
        preparingModel.clear();
        readyModel.clear();
        
        context.repository.getOrders().forEach(order -> {
            switch (order.getStatus()) {
                case "PREPARING" -> preparingModel.addElement(order);
                case "READY" -> readyModel.addElement(order);
            }
        });
        
        queueCount.setText((preparingModel.getSize() + readyModel.getSize()) + " active");
        
        if (currentSelectedOrder != null) {
            boolean found = selectInList(preparingList, preparingModel) ||
                            selectInList(readyList, readyModel);
            if (!found) {
                currentSelectedOrder = null;
                showDetails(null);
            }
        } else {
            showDetails(null);
        }
    }
    
    private boolean selectInList(JList<Order> list, DefaultListModel<Order> model) {
        for (int i = 0; i < model.size(); i++) {
            if (model.get(i).getId() == currentSelectedOrder.getId()) {
                list.setSelectedIndex(i);
                return true;
            }
        }
        return false;
    }

    private void executeAction(java.util.function.Consumer<Order> action) {
        if (currentSelectedOrder == null) return;
        try {
            action.accept(currentSelectedOrder);
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
        boolean preparing = order != null && "PREPARING".equals(order.getStatus());
        boolean ready = order != null && "READY".equals(order.getStatus());
        if (completeButton != null) completeButton.setEnabled(preparing);
        if (deliverButton != null) deliverButton.setEnabled(ready);
    }

    public void onOrderStatusChanged(Order order, String newStatus) {
        SwingUtilities.invokeLater(this::refresh);
    }

    private static final class KitchenOrderRenderer extends JPanel implements ListCellRenderer<Order> {
        private final JLabel id = new JLabel();
        private final JLabel status = new JLabel();
        private final JLabel meta = new JLabel();
        private final JLabel warning = new JLabel();
        private final JLabel total = new JLabel();

        private KitchenOrderRenderer() {
            setLayout(new BorderLayout(10, 4));
            setBorder(new EmptyBorder(10, 10, 10, 10));
            JPanel text = new JPanel(new GridLayout(4, 1, 0, 0));
            text.setOpaque(false);
            id.setFont(id.getFont().deriveFont(Font.BOLD, 17f));
            status.setFont(status.getFont().deriveFont(Font.BOLD, 12f));
            meta.setForeground(AppTheme.MUTED);
            warning.setFont(warning.getFont().deriveFont(Font.BOLD, 11f));
            warning.setForeground(AppTheme.DANGER);
            total.setFont(total.getFont().deriveFont(Font.BOLD, 15f));
            
            text.add(id);
            text.add(status);
            text.add(meta);
            text.add(warning);
            add(text, BorderLayout.CENTER);
            add(total, BorderLayout.EAST);
        }

        public Component getListCellRendererComponent(JList<? extends Order> list, Order order, int index, boolean selected, boolean focus) {
            id.setText("Order #" + order.getId());
            status.setText(order.getStatus());
            meta.setText(order.getItems().size() + " line(s) - " + order.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm")));
            total.setText(AppTheme.money(order.getTotalAmount()));
            
            long mins = Duration.between(order.getCreatedAt(), LocalDateTime.now()).toMinutes();
            if (mins >= 15 && !"READY".equals(order.getStatus())) {
                warning.setText("⚠️ WAITING " + mins + " MINS");
            } else {
                warning.setText("");
            }
            
            Color statusColor = switch (order.getStatus()) {
                case "PENDING" -> AppTheme.WARNING;
                case "PREPARING" -> AppTheme.SUCCESS;
                case "READY" -> AppTheme.ACCENT;
                default -> AppTheme.MUTED;
            };
            status.setForeground(statusColor);
            
            if (mins >= 15 && !"READY".equals(order.getStatus())) {
                setBackground(selected ? new Color(255, 230, 230) : new Color(255, 245, 245));
            } else {
                setBackground(selected ? new Color(255, 245, 235) : AppTheme.PANEL);
            }
            
            setOpaque(true);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER),
                    new EmptyBorder(8, 8, 8, 8)));
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
