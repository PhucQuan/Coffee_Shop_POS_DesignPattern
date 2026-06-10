package com.coffeeshop.presentation;

import com.coffeeshop.AppContext;
import com.coffeeshop.domain.model.Order;
import com.coffeeshop.domain.model.OrderItem;
import com.coffeeshop.domain.patterns.adapter.MomoAdapter;
import com.coffeeshop.domain.patterns.adapter.PaymentGateway;
import com.coffeeshop.domain.patterns.adapter.PaymentResult;
import com.coffeeshop.domain.patterns.adapter.VnpayAdapter;
import com.coffeeshop.domain.patterns.decorator.Beverage;
import com.coffeeshop.domain.patterns.strategy.NoDiscountStrategy;
import com.coffeeshop.domain.patterns.strategy.PercentDiscountStrategy;
import com.coffeeshop.infrastructure.MenuItemRecord;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Random;

public class POSView extends JFrame {
    private final AppContext context;
    private Order currentOrder;
    private final DefaultListModel<MenuItemRecord> menuModel = new DefaultListModel<>();
    private final DefaultListModel<String> billModel = new DefaultListModel<>();
    private final JList<MenuItemRecord> menuList = new JList<>(menuModel);
    private final JList<String> billList = new JList<>(billModel);
    private final JTextField searchField = new JTextField();
    private final JComboBox<String> categoryBox = new JComboBox<>(new String[]{"ALL", "COFFEE", "TEA", "MATCHA", "SMOOTHIE"});
    private final JCheckBox pearl = new JCheckBox("Tran chau");
    private final JCheckBox large = new JCheckBox("Size L");
    private final JCheckBox shot = new JCheckBox("Extra shot");
    private final JComboBox<String> discountBox = new JComboBox<>(new String[]{"No discount", "10%"});
    private final JLabel orderStatusLabel = new JLabel();
    private final JLabel subtotalLabel = new JLabel();
    private final JLabel discountLabel = new JLabel();
    private final JLabel totalLabel = new JLabel();
    private final JLabel paymentStatusLabel = new JLabel("Payment: idle");

    public POSView(AppContext context) {
        this.context = context;
        this.currentOrder = context.orderService.createOrder();
        setTitle("Coffee Shop POS - Cashier");
        setSize(1120, 680);
        setMinimumSize(new Dimension(980, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(AppShell.wrap(this, context, "Cashier",
                "Select drinks, customize toppings, send to kitchen, and process payment.",
                buildContent(), "POS", "Orders", "Receipt"));
        installShortcuts();
        refreshMenu();
        refreshBill();
    }

    private JComponent buildContent() {
        JPanel root = new JPanel(new BorderLayout(14, 14));
        root.setOpaque(false);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildMenuPanel(), buildBillPanel());
        split.setResizeWeight(0.55);
        split.setDividerSize(8);
        split.setBorder(null);
        root.add(split, BorderLayout.CENTER);
        return root;
    }

    private JPanel buildMenuPanel() {
        JPanel panel = card(new BorderLayout(10, 10));
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(sectionHeader("Menu", "Search and select a beverage"), BorderLayout.WEST);
        orderStatusLabel.setFont(orderStatusLabel.getFont().deriveFont(Font.BOLD, 14f));
        orderStatusLabel.setForeground(AppTheme.SUCCESS);
        top.add(orderStatusLabel, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        JPanel filters = new JPanel(new BorderLayout(8, 8));
        filters.setOpaque(false);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 226, 232)),
                new EmptyBorder(10, 12, 10, 12)
        ));
        categoryBox.setPreferredSize(new Dimension(150, 42));
        JButton refresh = secondaryButton("Refresh");
        JPanel right = new JPanel(new BorderLayout(8, 8));
        right.setOpaque(false);
        right.add(categoryBox, BorderLayout.CENTER);
        right.add(refresh, BorderLayout.EAST);
        filters.add(searchField, BorderLayout.CENTER);
        filters.add(right, BorderLayout.EAST);

        menuList.setCellRenderer(new MenuRenderer());
        menuList.setFixedCellHeight(70);
        menuList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane menuScroll = new JScrollPane(menuList);
        menuScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 226, 232)));

        JPanel options = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        options.setOpaque(false);
        options.setBorder(BorderFactory.createTitledBorder("Options"));
        options.add(pearl);
        options.add(large);
        options.add(shot);

        JButton add = primaryButton("Add to order  (F1)");
        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.setOpaque(false);
        bottom.add(options, BorderLayout.CENTER);
        bottom.add(add, BorderLayout.SOUTH);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setOpaque(false);
        center.add(filters, BorderLayout.NORTH);
        center.add(menuScroll, BorderLayout.CENTER);
        center.add(bottom, BorderLayout.SOUTH);
        panel.add(center, BorderLayout.CENTER);

        refresh.addActionListener(e -> refreshMenu());
        add.addActionListener(e -> addSelectedItem());
        menuList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) addSelectedItem();
            }
        });
        searchField.getDocument().addDocumentListener((SimpleDocumentListener) this::refreshMenu);
        categoryBox.addActionListener(e -> refreshMenu());
        return panel;
    }

    private JPanel buildBillPanel() {
        JPanel panel = card(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(430, 0));
        panel.add(sectionHeader("Current bill", "Review order and payment"), BorderLayout.NORTH);

        billList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        billList.setCellRenderer(new BillRenderer());
        JScrollPane billScroll = new JScrollPane(billList);
        billScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 226, 232)));

        JPanel summary = new JPanel(new GridLayout(3, 1, 0, 8));
        summary.setOpaque(false);
        summary.add(summaryLine("Subtotal", subtotalLabel));
        summary.add(summaryLine("Discount", discountLabel));
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 22f));
        totalLabel.setForeground(new Color(255, 128, 76));
        summary.add(summaryLine("Total", totalLabel));

        JPanel actions = new JPanel(new GridLayout(0, 2, 8, 8));
        actions.setOpaque(false);
        JButton newOrder = secondaryButton("New order");
        JButton apply = secondaryButton("Apply discount");
        JButton sendKitchen = actionButton("Send kitchen", new Color(184, 111, 20));
        JButton ready = actionButton("Mark ready", new Color(19, 132, 76));
        JButton momo = primaryButton("Pay Momo  (F2)");
        JButton vnpay = primaryButton("Pay VNPay");
        JButton receipt = secondaryButton("Receipt preview");
        actions.add(discountBox);
        actions.add(apply);
        actions.add(newOrder);
        actions.add(sendKitchen);
        actions.add(ready);
        actions.add(receipt);
        actions.add(momo);
        actions.add(vnpay);

        JPanel bottom = new JPanel(new BorderLayout(10, 10));
        bottom.setOpaque(false);
        paymentStatusLabel.setForeground(new Color(100, 112, 125));
        bottom.add(summary, BorderLayout.NORTH);
        bottom.add(actions, BorderLayout.CENTER);
        bottom.add(paymentStatusLabel, BorderLayout.SOUTH);

        panel.add(billScroll, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        newOrder.addActionListener(e -> newOrder());
        apply.addActionListener(e -> applyDiscount());
        sendKitchen.addActionListener(e -> changeState(() -> context.orderService.sendToKitchen(currentOrder)));
        ready.addActionListener(e -> changeState(() -> context.orderService.markReady(currentOrder)));
        momo.addActionListener(e -> pay(new MomoAdapter(new Random(1))));
        vnpay.addActionListener(e -> pay(new VnpayAdapter(new Random(1))));
        receipt.addActionListener(e -> showReceipt());
        return panel;
    }

    private JPanel sectionHeader(String title, String subtitle) {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 2));
        panel.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(t.getFont().deriveFont(Font.BOLD, 18f));
        JLabel s = new JLabel(subtitle);
        s.setForeground(new Color(100, 112, 125));
        panel.add(t);
        panel.add(s);
        return panel;
    }

    private JPanel card(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 229, 235)),
                new EmptyBorder(14, 14, 14, 14)
        ));
        return panel;
    }

    private JButton primaryButton(String text) {
        return actionButton(text, new Color(255, 128, 76));
    }

    private JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(35, 45, 55));
        button.setFont(button.getFont().deriveFont(Font.BOLD, 13f));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(196, 210, 225)),
                new EmptyBorder(10, 12, 10, 12)
        ));
        return button;
    }

    private JButton actionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 13f));
        button.setBorder(new EmptyBorder(11, 14, 11, 14));
        return button;
    }

    private JPanel summaryLine(String label, JLabel value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel left = new JLabel(label);
        left.setForeground(new Color(100, 112, 125));
        value.setHorizontalAlignment(SwingConstants.RIGHT);
        row.add(left, BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);
        return row;
    }

    private void installShortcuts() {
        JRootPane root = getRootPane();
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "addItem");
        root.getActionMap().put("addItem", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { addSelectedItem(); }
        });
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "payMomo");
        root.getActionMap().put("payMomo", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { pay(new MomoAdapter(new Random(1))); }
        });
    }

    private void refreshMenu() {
        String query = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        String category = String.valueOf(categoryBox.getSelectedItem());
        List<MenuItemRecord> items = context.menuService.getActiveMenu();
        menuModel.clear();
        items.stream()
                .filter(item -> "ALL".equals(category) || item.getCategory().equals(category))
                .filter(item -> query.isEmpty() || item.getName().toLowerCase().contains(query))
                .forEach(menuModel::addElement);
    }

    private void newOrder() {
        currentOrder = context.orderService.createOrder();
        context.orderService.setDiscountStrategy(new NoDiscountStrategy());
        pearl.setSelected(false);
        large.setSelected(false);
        shot.setSelected(false);
        discountBox.setSelectedItem("No discount");
        paymentStatusLabel.setText("Payment: idle");
        refreshBill();
    }

    private void addSelectedItem() {
        MenuItemRecord item = menuList.getSelectedValue();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Please select a beverage first.");
            return;
        }
        try {
            Beverage beverage = context.menuService.createBeverage(item);
            if (pearl.isSelected()) beverage = context.menuService.applyTopping(beverage, "Tran chau");
            if (large.isSelected()) beverage = context.menuService.applyTopping(beverage, "Size L");
            if (shot.isSelected()) beverage = context.menuService.applyTopping(beverage, "Extra shot");
            context.orderService.addItem(currentOrder, item.getId(), beverage, 1, "");
            refreshBill();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void applyDiscount() {
        context.orderService.setDiscountStrategy("10%".equals(discountBox.getSelectedItem())
                ? new PercentDiscountStrategy(10)
                : new NoDiscountStrategy());
        context.orderService.recalculate(currentOrder);
        refreshBill();
    }

    private void pay(PaymentGateway gateway) {
        paymentStatusLabel.setText("Payment: processing " + gateway.getGatewayName() + "...");
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingWorker<PaymentResult, Void> worker = new SwingWorker<>() {
            protected PaymentResult doInBackground() throws Exception {
                Thread.sleep(900);
                return context.paymentService.pay(currentOrder, gateway);
            }

            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    PaymentResult result = get();
                    paymentStatusLabel.setText(result.isSuccess() ? "Payment: success" : "Payment: failed");
                    JOptionPane.showMessageDialog(POSView.this, result.getMessage() + "\n" + result.getTransactionCode());
                    refreshBill();
                    if (result.isSuccess()) showReceipt();
                } catch (Exception ex) {
                    paymentStatusLabel.setText("Payment: error");
                    JOptionPane.showMessageDialog(POSView.this, ex.getCause() == null ? ex.getMessage() : ex.getCause().getMessage());
                }
            }
        };
        worker.execute();
    }

    private void showReceipt() {
        new ReceiptPreviewDialog(this, context.receiptService.buildReceipt(currentOrder), context.receiptImageService).setVisible(true);
    }

    private void changeState(Runnable action) {
        try {
            action.run();
            refreshBill();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void refreshBill() {
        billModel.clear();
        if (currentOrder.getItems().isEmpty()) {
            billModel.addElement("No items yet. Select a beverage from the menu.");
        } else {
            for (OrderItem item : currentOrder.getItems()) billModel.addElement(item.toString());
        }
        orderStatusLabel.setText("Order #" + currentOrder.getId() + " - " + currentOrder.getStatus());
        subtotalLabel.setText(AppTheme.money(currentOrder.getSubtotal()));
        discountLabel.setText("-" + AppTheme.money(currentOrder.getDiscountAmount()));
        totalLabel.setText(AppTheme.money(currentOrder.getTotalAmount()));
    }

    private static class MenuRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
            MenuItemRecord item = (MenuItemRecord) value;
            label.setText("<html><b>" + item.getName() + "</b><br><span style='color:#667085'>"
                    + item.getCategory() + " - " + AppTheme.money(item.getBasePrice()) + "</span></html>");
            label.setIcon(DrinkIconFactory.create(item.getCategory(), item.getName(), 52));
            label.setIconTextGap(14);
            label.setBorder(new EmptyBorder(8, 10, 8, 10));
            label.setBackground(selected ? new Color(232, 244, 238) : Color.WHITE);
            label.setForeground(new Color(30, 45, 60));
            return label;
        }
    }

    private static class BillRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
            label.setBorder(new EmptyBorder(8, 10, 8, 10));
            label.setBackground(index % 2 == 0 ? new Color(250, 251, 252) : Color.WHITE);
            label.setForeground(new Color(30, 45, 60));
            return label;
        }
    }

    @FunctionalInterface
    private interface SimpleDocumentListener extends javax.swing.event.DocumentListener {
        void update();
        default void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
        default void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
        default void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
    }
}
