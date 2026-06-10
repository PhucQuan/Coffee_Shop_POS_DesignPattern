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
    private final DefaultListModel<String> billModel = new DefaultListModel<>();
    private final JList<String> billList = new JList<>(billModel);
    private final JPanel menuGrid = new JPanel(new WrapLayout(FlowLayout.LEFT, 14, 14));
    private final JTextField searchField = new JTextField();
    private final ButtonGroup categoryGroup = new ButtonGroup();
    private final JCheckBox pearl = new JCheckBox("Tran chau");
    private final JCheckBox large = new JCheckBox("Size L");
    private final JCheckBox shot = new JCheckBox("Extra shot");
    private final JComboBox<String> discountBox = new JComboBox<>(new String[]{"No discount", "10%"});
    private final JLabel orderStatusLabel = new JLabel();
    private final JLabel subtotalLabel = new JLabel();
    private final JLabel discountLabel = new JLabel();
    private final JLabel totalLabel = new JLabel();
    private final JLabel paymentStatusLabel = new JLabel("Payment: idle");
    private MenuItemRecord selectedMenuItem;
    private String selectedCategory = "ALL";

    public POSView(AppContext context) {
        this.context = context;
        this.currentOrder = context.orderService.createOrder();
        setTitle("Coffee Shop POS - Cashier");
        setSize(1280, 760);
        setMinimumSize(new Dimension(1180, 700));
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
        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setOpaque(false);

        root.add(buildMenuPanel(), BorderLayout.CENTER);
        root.add(buildBillPanel(), BorderLayout.EAST);
        return root;
    }

    private JPanel buildMenuPanel() {
        JPanel panel = card(new BorderLayout(14, 14));
        panel.setMinimumSize(new Dimension(680, 0));
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
                BorderFactory.createLineBorder(AppTheme.BORDER),
                new EmptyBorder(10, 12, 10, 12)
        ));
        JButton refresh = secondaryButton("Refresh menu");
        JPanel right = new JPanel(new BorderLayout(8, 8));
        right.setOpaque(false);
        right.add(refresh, BorderLayout.EAST);
        filters.add(searchField, BorderLayout.CENTER);
        filters.add(right, BorderLayout.EAST);

        JPanel categories = new JPanel(new GridLayout(1, 5, 10, 0));
        categories.setOpaque(false);
        categories.setPreferredSize(new Dimension(0, 78));
        addCategoryButton(categories, "ALL", "All", "16 items");
        addCategoryButton(categories, "COFFEE", "Coffee", "10 items");
        addCategoryButton(categories, "TEA", "Tea", "4 items");
        addCategoryButton(categories, "MATCHA", "Matcha", "2 items");
        addCategoryButton(categories, "SMOOTHIE", "Smoothie", "2 items");

        menuGrid.setOpaque(false);
        JScrollPane menuScroll = new JScrollPane(menuGrid);
        menuScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(237, 226, 214)),
                new EmptyBorder(6, 6, 6, 6)
        ));
        menuScroll.getViewport().setBackground(AppTheme.SURFACE);
        menuScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        menuScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        menuScroll.getVerticalScrollBar().setUnitIncrement(18);

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
        JPanel menuBody = new JPanel(new BorderLayout(0, 14));
        menuBody.setOpaque(false);
        menuBody.add(categories, BorderLayout.NORTH);
        menuBody.add(menuScroll, BorderLayout.CENTER);
        center.add(menuBody, BorderLayout.CENTER);
        center.add(bottom, BorderLayout.SOUTH);
        panel.add(center, BorderLayout.CENTER);

        refresh.addActionListener(e -> refreshMenu());
        add.addActionListener(e -> addSelectedItem());
        searchField.getDocument().addDocumentListener((SimpleDocumentListener) this::refreshMenu);
        return panel;
    }

    private JPanel buildBillPanel() {
        JPanel panel = card(new BorderLayout(14, 14));
        panel.setPreferredSize(new Dimension(430, 0));
        panel.setMinimumSize(new Dimension(410, 0));
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        JLabel cartTitle = new JLabel("Cart");
        cartTitle.setForeground(AppTheme.TEXT);
        cartTitle.setFont(cartTitle.getFont().deriveFont(Font.BOLD, 22f));
        JLabel cartSubtitle = AppTheme.muted("Review selected drinks and checkout");
        cartTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        cartSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.add(cartTitle);
        top.add(Box.createVerticalStrut(4));
        top.add(cartSubtitle);
        panel.add(top, BorderLayout.NORTH);

        billList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        billList.setCellRenderer(new BillRenderer());
        JScrollPane billScroll = new JScrollPane(billList);
        billScroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        billScroll.setPreferredSize(new Dimension(0, 150));

        JPanel summary = new JPanel(new GridLayout(3, 1, 0, 8));
        summary.setOpaque(false);
        summary.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 0, AppTheme.BORDER),
                new EmptyBorder(12, 0, 12, 0)
        ));
        summary.add(summaryLine("Subtotal", subtotalLabel));
        summary.add(summaryLine("Discount", discountLabel));
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 22f));
        totalLabel.setForeground(AppTheme.ACCENT);
        summary.add(summaryLine("Total", totalLabel));

        JPanel actions = new JPanel(new GridLayout(0, 1, 8, 8));
        actions.setOpaque(false);
        JButton newOrder = secondaryButton("New order");
        JButton apply = secondaryButton("Apply discount");
        JButton sendKitchen = actionButton("Send kitchen", new Color(184, 111, 20));
        JButton ready = actionButton("Mark ready", new Color(19, 132, 76));
        JButton momo = primaryButton("Pay Momo  (F2)");
        JButton vnpay = primaryButton("Pay VNPay");
        JButton receipt = secondaryButton("Receipt preview");
        JLabel discountTitle = actionGroupLabel("Discount");
        JLabel flowTitle = actionGroupLabel("Order flow");
        JLabel paymentTitle = actionGroupLabel("Payment");
        JPanel discountRow = new JPanel(new GridLayout(1, 2, 8, 0));
        discountRow.setOpaque(false);
        discountRow.setPreferredSize(new Dimension(0, 44));
        discountRow.add(discountBox);
        discountRow.add(apply);
        actions.add(discountTitle);
        actions.add(discountRow);
        actions.add(flowTitle);
        actions.add(newOrder);
        actions.add(sendKitchen);
        actions.add(ready);
        actions.add(receipt);
        actions.add(paymentTitle);
        actions.add(momo);
        actions.add(vnpay);

        JPanel bottom = new JPanel(new BorderLayout(10, 10));
        bottom.setOpaque(false);
        paymentStatusLabel.setForeground(new Color(100, 112, 125));
        paymentStatusLabel.setBorder(new EmptyBorder(6, 0, 0, 0));
        JPanel lower = new JPanel(new BorderLayout(0, 12));
        lower.setOpaque(false);
        lower.add(summary, BorderLayout.NORTH);
        lower.add(actions, BorderLayout.CENTER);
        bottom.add(lower, BorderLayout.CENTER);
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

    private JLabel actionGroupLabel(String text) {
        JLabel label = AppTheme.muted(text);
        label.setBorder(new EmptyBorder(4, 0, 0, 0));
        label.setFont(label.getFont().deriveFont(Font.BOLD, 12f));
        return label;
    }

    private void addCategoryButton(JPanel parent, String key, String title, String subtitle) {
        JToggleButton button = new JToggleButton("<html><b>" + title + "</b><br><span style='font-size:9px'>" + subtitle + "</span></html>");
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(10, 12, 10, 12));
        button.setForeground(AppTheme.TEXT);
        button.setBackground(AppTheme.PANEL);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 12f));
        button.setPreferredSize(new Dimension(0, 62));
        button.addActionListener(e -> {
            selectedCategory = key;
            refreshMenu();
        });
        categoryGroup.add(button);
        parent.add(button);
        if ("ALL".equals(key)) button.setSelected(true);
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
        return actionButton(text, AppTheme.ACCENT);
    }

    private JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(AppTheme.CREAM);
        button.setForeground(AppTheme.TEXT);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 13f));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
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
        left.setForeground(AppTheme.MUTED);
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
        List<MenuItemRecord> items = context.menuService.getActiveMenu();
        menuGrid.removeAll();
        List<MenuItemRecord> filtered = items.stream()
                .filter(item -> "ALL".equals(selectedCategory) || item.getCategory().equals(selectedCategory))
                .filter(item -> query.isEmpty() || item.getName().toLowerCase().contains(query))
                .toList();
        for (MenuItemRecord item : filtered) menuGrid.add(menuCard(item));
        menuGrid.revalidate();
        menuGrid.repaint();
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
        MenuItemRecord item = selectedMenuItem;
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
            refreshMenu();
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
        PaymentDialog dialog = new PaymentDialog(this, currentOrder, gateway, context.paymentService);
        dialog.setVisible(true);
        PaymentResult result = dialog.getResult();
        if (result == null) {
            paymentStatusLabel.setText("Payment: cancelled");
            return;
        }
        paymentStatusLabel.setText(result.isSuccess() ? "Payment: success" : "Payment: failed");
        refreshBill();
        if (result.isSuccess()) showReceipt();
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
            billModel.addElement("No items yet");
            billModel.addElement("Select a drink card from the menu.");
        } else {
            for (OrderItem item : currentOrder.getItems()) billModel.addElement(item.toString());
        }
        orderStatusLabel.setText("Order #" + currentOrder.getId() + " - " + currentOrder.getStatus());
        subtotalLabel.setText(AppTheme.money(currentOrder.getSubtotal()));
        discountLabel.setText("-" + AppTheme.money(currentOrder.getDiscountAmount()));
        totalLabel.setText(AppTheme.money(currentOrder.getTotalAmount()));
    }

    private JPanel menuCard(MenuItemRecord item) {
        boolean selected = selectedMenuItem != null && selectedMenuItem.getId() == item.getId();
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(selected ? new Color(255, 241, 225) : AppTheme.PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(selected ? AppTheme.ACCENT : new Color(232, 220, 207), selected ? 2 : 1),
                new EmptyBorder(14, 14, 14, 14)
        ));
        card.setPreferredSize(new Dimension(185, 196));

        JLabel icon = new JLabel(DrinkIconFactory.create(item.getCategory(), item.getName(), 66));
        icon.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(icon, BorderLayout.NORTH);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel name = new JLabel(item.getName());
        name.setForeground(AppTheme.TEXT);
        name.setFont(name.getFont().deriveFont(Font.BOLD, 15f));
        JLabel category = AppTheme.muted(item.getCategory());
        JLabel price = new JLabel(AppTheme.money(item.getBasePrice()));
        price.setForeground(AppTheme.PRIMARY);
        price.setFont(price.getFont().deriveFont(Font.BOLD, 15f));
        text.add(name);
        text.add(Box.createVerticalStrut(4));
        text.add(category);
        text.add(Box.createVerticalStrut(8));
        text.add(price);

        JButton add = new JButton("+");
        add.setFocusPainted(false);
        add.setForeground(Color.WHITE);
        add.setBackground(AppTheme.SUCCESS);
        add.setFont(add.getFont().deriveFont(Font.BOLD, 24f));
        add.setBorder(new EmptyBorder(0, 0, 3, 0));
        add.setPreferredSize(new Dimension(48, 44));
        add.addActionListener(e -> {
            selectedMenuItem = item;
            addSelectedItem();
        });

        JPanel bottom = new JPanel(new BorderLayout(8, 0));
        bottom.setOpaque(false);
        bottom.add(text, BorderLayout.CENTER);
        bottom.add(add, BorderLayout.EAST);
        card.add(bottom, BorderLayout.CENTER);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectedMenuItem = item;
                if (e.getClickCount() == 2) {
                    addSelectedItem();
                } else {
                    refreshMenu();
                }
            }
        });
        return card;
    }

    private static class BillRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
            label.setBorder(new EmptyBorder(8, 10, 8, 10));
            label.setBackground(index % 2 == 0 ? AppTheme.SURFACE : Color.WHITE);
            label.setForeground(AppTheme.TEXT);
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

    private static final class WrapLayout extends FlowLayout {
        private WrapLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }

        public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }

        public Dimension minimumLayoutSize(Container target) {
            Dimension minimum = layoutSize(target, false);
            minimum.width -= getHgap() + 1;
            return minimum;
        }

        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getWidth();
                if (targetWidth == 0 && target.getParent() != null) targetWidth = target.getParent().getWidth();
                if (targetWidth == 0) targetWidth = 700;
                Insets insets = target.getInsets();
                int horizontalInsetsAndGap = insets.left + insets.right + getHgap() * 2;
                int maxWidth = targetWidth - horizontalInsetsAndGap;
                Dimension dimension = new Dimension(0, 0);
                int rowWidth = 0;
                int rowHeight = 0;

                for (int i = 0; i < target.getComponentCount(); i++) {
                    Component component = target.getComponent(i);
                    if (!component.isVisible()) continue;
                    Dimension size = preferred ? component.getPreferredSize() : component.getMinimumSize();
                    if (rowWidth + size.width > maxWidth && rowWidth > 0) {
                        addRow(dimension, rowWidth, rowHeight);
                        rowWidth = 0;
                        rowHeight = 0;
                    }
                    if (rowWidth != 0) rowWidth += getHgap();
                    rowWidth += size.width;
                    rowHeight = Math.max(rowHeight, size.height);
                }
                addRow(dimension, rowWidth, rowHeight);
                dimension.width += horizontalInsetsAndGap;
                dimension.height += insets.top + insets.bottom + getVgap() * 2;
                return dimension;
            }
        }

        private void addRow(Dimension dimension, int rowWidth, int rowHeight) {
            dimension.width = Math.max(dimension.width, rowWidth);
            if (dimension.height > 0) dimension.height += getVgap();
            dimension.height += rowHeight;
        }
    }
}
