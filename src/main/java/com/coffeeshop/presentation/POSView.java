package com.coffeeshop.presentation;

import com.coffeeshop.AppContext;
import com.coffeeshop.domain.model.Order;
import com.coffeeshop.domain.model.OrderItem;
import com.coffeeshop.domain.model.Topping;
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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class POSView extends JFrame {
    private final AppContext context;
    private Order currentOrder;
    private final CardLayout workspaceLayout = new CardLayout();
    private final JPanel workspacePanel = new JPanel(workspaceLayout);
    private final DefaultListModel<String> billModel = new DefaultListModel<>();
    private final JList<String> billList = new JList<>(billModel);
    private final List<OrderItem> billItems = new ArrayList<>();
    private final DefaultListModel<Order> cashierOrderModel = new DefaultListModel<>();
    private final JList<Order> cashierOrderList = new JList<>(cashierOrderModel);
    private final JTextArea cashierOrderDetailArea = new JTextArea();
    private final JTextArea receiptArea = new JTextArea();
    private final JPanel menuGrid = new JPanel(new WrapLayout(FlowLayout.LEFT, 20, 20));
    private final JTextField searchField = new JTextField();
    private final ButtonGroup categoryGroup = new ButtonGroup();
    private final List<Topping> activeToppings = new ArrayList<>();
    private final DefaultTableModel toppingTableModel = new DefaultTableModel(new Object[]{"", "Topping", "Price"}, 0) {
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 0 ? Boolean.class : String.class;
        }

        public boolean isCellEditable(int row, int column) {
            return column == 0;
        }
    };
    private final JTable toppingTable = new JTable(toppingTableModel);
    private final JComboBox<String> discountBox = new JComboBox<>(new String[]{"No discount", "10%"});
    private final JLabel orderStatusLabel = new JLabel();
    private final JLabel subtotalLabel = new JLabel();
    private final JLabel discountLabel = new JLabel();
    private final JLabel totalLabel = new JLabel();
    private final JLabel paymentStatusLabel = new JLabel("Payment: idle");
    private final JLabel selectedIconLabel = new JLabel();
    private final JLabel selectedNameLabel = new JLabel("Select a drink");
    private final JLabel selectedCategoryLabel = new JLabel("Pick from menu");
    private final JLabel selectedPriceLabel = new JLabel(AppTheme.money(0));
    private JButton increaseQtyButton;
    private JButton decreaseQtyButton;
    private JButton removeItemButton;
    private JButton cancelOrderButton;
    private JButton sendKitchenButton;
    private JButton readyButton;
    private JButton momoButton;
    private JButton vnpayButton;
    private JButton applyDiscountButton;
    private MenuItemRecord selectedMenuItem;
    private String selectedCategory = "ALL";

    public POSView(AppContext context) {
        this.context = context;
        this.currentOrder = context.orderService.createOrder();
        setTitle("Coffee Shop POS - Cashier");
        setSize(1540, 800);
        setMinimumSize(new Dimension(1280, 720));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(AppShell.wrap(this, context, "Cashier",
                "",
                buildWorkspace(), this::showCashierPage, "POS", "Orders", "Receipt"));
        installShortcuts();
        refreshMenu();
        refreshBill();
    }

    private JComponent buildWorkspace() {
        workspacePanel.setOpaque(false);
        workspacePanel.add(buildContent(), "POS");
        workspacePanel.add(buildOrdersPage(), "Orders");
        workspacePanel.add(buildReceiptPage(), "Receipt");
        return workspacePanel;
    }

    private void showCashierPage(String page) {
        if ("Orders".equals(page)) refreshOrdersPage();
        if ("Receipt".equals(page)) refreshReceiptPage();
        workspaceLayout.show(workspacePanel, page);
    }

    private JComponent buildContent() {
        JPanel root = new JPanel(new BorderLayout(14, 24));
        root.setOpaque(false);

        root.add(buildSelectedPanel(), BorderLayout.WEST);
        JPanel orderWorkspace = new JPanel(new BorderLayout(14, 0));
        orderWorkspace.setOpaque(false);
        orderWorkspace.add(buildMenuPanel(), BorderLayout.CENTER);
        orderWorkspace.add(buildToppingPanel(), BorderLayout.EAST);
        root.add(orderWorkspace, BorderLayout.CENTER);
        root.add(buildBillPanel(), BorderLayout.EAST);
        return root;
    }

    private JPanel buildOrdersPage() {
        JPanel panel = card(new BorderLayout(12, 12));
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(sectionHeader("Orders", "Track active and completed orders created in this session."), BorderLayout.WEST);
        JButton refresh = secondaryButton("Refresh orders");
        top.add(refresh, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        cashierOrderDetailArea.setEditable(false);
        cashierOrderDetailArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        cashierOrderDetailArea.setBorder(new EmptyBorder(12, 12, 12, 12));
        cashierOrderList.setCellRenderer(new OrderListRenderer());
        cashierOrderList.setFixedCellHeight(76);
        cashierOrderList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cashierOrderDetailArea.setText(orderDetails(cashierOrderList.getSelectedValue()));
            }
        });
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(cashierOrderList), new JScrollPane(cashierOrderDetailArea));
        split.setDividerLocation(340);
        split.setBorder(null);
        panel.add(split, BorderLayout.CENTER);
        refresh.addActionListener(e -> refreshOrdersPage());
        refreshOrdersPage();
        return panel;
    }

    private JPanel buildReceiptPage() {
        JPanel panel = card(new BorderLayout(12, 12));
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(sectionHeader("Receipt", "Preview the current order receipt and export it when paid."), BorderLayout.WEST);
        JButton preview = secondaryButton("Open preview");
        top.add(preview, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        receiptArea.setEditable(false);
        receiptArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        receiptArea.setBorder(new EmptyBorder(14, 14, 14, 14));
        panel.add(new JScrollPane(receiptArea), BorderLayout.CENTER);
        preview.addActionListener(e -> showReceipt());
        refreshReceiptPage();
        return panel;
    }

    private JPanel buildSelectedPanel() {
        // Warm espresso dark — softer than pure black, matches sidebar tone
        JPanel panel = AppTheme.roundedPanel(new BorderLayout(0, 14),
                new Color(50, 24, 6), null, 14, new Insets(22, 20, 20, 20));
        panel.setPreferredSize(new Dimension(224, 0));
        panel.setMinimumSize(new Dimension(210, 0));

        JPanel title = new JPanel();
        title.setOpaque(false);
        title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));
        JLabel eyebrow = new JLabel("CURRENT DRINK");
        eyebrow.setForeground(AppTheme.PRIMARY);
        eyebrow.setFont(eyebrow.getFont().deriveFont(Font.BOLD, 9f));
        selectedNameLabel.setForeground(new Color(248, 238, 220));
        selectedNameLabel.setFont(selectedNameLabel.getFont().deriveFont(Font.BOLD, 19f));
        selectedCategoryLabel.setForeground(new Color(148, 122, 94));
        selectedCategoryLabel.setFont(selectedCategoryLabel.getFont().deriveFont(Font.PLAIN, 12f));
        title.add(eyebrow);
        title.add(Box.createVerticalStrut(7));
        title.add(selectedNameLabel);
        title.add(Box.createVerticalStrut(3));
        title.add(selectedCategoryLabel);
        panel.add(title, BorderLayout.NORTH);

        JPanel middle = new JPanel();
        middle.setOpaque(false);
        middle.setLayout(new BoxLayout(middle, BoxLayout.Y_AXIS));
        selectedIconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectedIconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        selectedPriceLabel.setForeground(new Color(234, 179, 8));   // honey gold
        selectedPriceLabel.setFont(selectedPriceLabel.getFont().deriveFont(Font.BOLD, 22f));
        selectedPriceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        middle.add(Box.createVerticalGlue());
        middle.add(selectedIconLabel);
        middle.add(Box.createVerticalStrut(14));
        middle.add(selectedPriceLabel);
        middle.add(Box.createVerticalGlue());
        panel.add(middle, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        JButton add = AppTheme.button("Add to cart  (F1)", AppTheme.PRIMARY);
        add.setAlignmentX(Component.LEFT_ALIGNMENT);
        add.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        add.addActionListener(e -> addSelectedItem());
        bottom.add(add);
        panel.add(bottom, BorderLayout.SOUTH);

        updateSelectedPanel();
        return panel;
    }

    private JPanel buildMenuPanel() {
        JPanel panel = card(new BorderLayout(24, 24));
        panel.setMinimumSize(new Dimension(420, 0));
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(singleLineHeader("Menu"), BorderLayout.WEST);
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

        JPanel categories = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        categories.setOpaque(false);
        categories.setPreferredSize(new Dimension(0, 46));
        addCategoryButton(categories, "ALL", "All", "16 items");
        addCategoryButton(categories, "COFFEE", "Coffee", "10 items");
        addCategoryButton(categories, "TEA", "Tea", "4 items");
        addCategoryButton(categories, "MATCHA", "Matcha", "2 items");
        addCategoryButton(categories, "SMOOTHIE", "Smoothie", "2 items");

        menuGrid.setOpaque(false);
        JScrollPane menuScroll = new JScrollPane(menuGrid);
        menuScroll.setBorder(new EmptyBorder(4, 4, 4, 4));
        menuScroll.getViewport().setBackground(AppTheme.SURFACE);
        menuScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        menuScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        menuScroll.getVerticalScrollBar().setUnitIncrement(18);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setOpaque(false);
        center.add(filters, BorderLayout.NORTH);
        JPanel menuBody = new JPanel(new BorderLayout(0, 14));
        menuBody.setOpaque(false);
        menuBody.add(categories, BorderLayout.NORTH);
        menuBody.add(menuScroll, BorderLayout.CENTER);
        center.add(menuBody, BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);

        refresh.addActionListener(e -> refreshMenu());
        searchField.getDocument().addDocumentListener((SimpleDocumentListener) this::refreshMenu);
        return panel;
    }

    private JPanel buildToppingPanel() {
        JPanel panel = card(new BorderLayout(0, 14));
        panel.setPreferredSize(new Dimension(238, 0));
        panel.setMinimumSize(new Dimension(218, 0));
        panel.add(singleLineHeader("Toppings"), BorderLayout.NORTH);

        toppingTable.setRowHeight(38);
        toppingTable.setShowGrid(false);
        toppingTable.setIntercellSpacing(new Dimension(0, 0));
        toppingTable.setSelectionBackground(new Color(255, 241, 225));
        toppingTable.setSelectionForeground(AppTheme.TEXT);
        toppingTable.setForeground(AppTheme.TEXT);
        toppingTable.setBackground(AppTheme.SURFACE);
        toppingTable.getTableHeader().setReorderingAllowed(false);
        toppingTable.getTableHeader().setFont(toppingTable.getTableHeader().getFont().deriveFont(Font.BOLD, 12f));
        toppingTable.getTableHeader().setForeground(AppTheme.MUTED);
        toppingTable.getTableHeader().setBackground(AppTheme.PANEL);
        TableColumnModel columns = toppingTable.getColumnModel();
        columns.getColumn(0).setPreferredWidth(36);
        columns.getColumn(0).setMaxWidth(42);
        columns.getColumn(1).setPreferredWidth(132);
        columns.getColumn(2).setPreferredWidth(82);

        JScrollPane scroll = new JScrollPane(toppingTable);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        scroll.getViewport().setBackground(AppTheme.SURFACE);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(1, 2, 8, 0));
        actions.setOpaque(false);
        JButton clear = secondaryButton("Clear");
        JButton refresh = secondaryButton("Refresh");
        clear.addActionListener(e -> clearSelectedToppings());
        refresh.addActionListener(e -> refreshToppings());
        actions.add(clear);
        actions.add(refresh);
        panel.add(actions, BorderLayout.SOUTH);

        refreshToppings();
        return panel;
    }

    private JPanel buildBillPanel() {
        JPanel panel = card(new BorderLayout(0, 16));
        panel.setPreferredSize(new Dimension(500, 0));
        panel.setMinimumSize(new Dimension(440, 0));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel cartTitle = new JLabel("Cart");
        cartTitle.setForeground(AppTheme.TEXT);
        cartTitle.setFont(cartTitle.getFont().deriveFont(Font.BOLD, 22f));
        JPanel titleStack = new JPanel();
        titleStack.setOpaque(false);
        titleStack.setLayout(new BoxLayout(titleStack, BoxLayout.Y_AXIS));
        titleStack.add(cartTitle);
        top.add(titleStack, BorderLayout.WEST);
        panel.add(top, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        billList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        billList.setCellRenderer(new BillRenderer());
        JScrollPane billScroll = new JScrollPane(billList);
        billScroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        billScroll.setPreferredSize(new Dimension(410, 106));
        billScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 106));
        billScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(billScroll);
        center.add(Box.createVerticalStrut(6));

        JPanel itemActions = new JPanel(new GridLayout(1, 3, 8, 0));
        itemActions.setOpaque(false);
        itemActions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        itemActions.setAlignmentX(Component.LEFT_ALIGNMENT);
        decreaseQtyButton = secondaryButton("- 1");
        increaseQtyButton = secondaryButton("+ 1");
        removeItemButton = secondaryButton("Remove");
        itemActions.add(decreaseQtyButton);
        itemActions.add(increaseQtyButton);
        itemActions.add(removeItemButton);
        center.add(itemActions);
        center.add(Box.createVerticalStrut(6));

        JPanel summary = new JPanel(new GridLayout(3, 1, 0, 4));
        summary.setOpaque(false);
        summary.setAlignmentX(Component.LEFT_ALIGNMENT);
        summary.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        summary.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 0, AppTheme.BORDER),
                new EmptyBorder(6, 0, 6, 0)
        ));
        summary.add(summaryLine("Subtotal", subtotalLabel));
        summary.add(summaryLine("Discount", discountLabel));
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 20f));
        totalLabel.setForeground(AppTheme.ACCENT);
        summary.add(summaryLine("Total", totalLabel));
        center.add(summary);
        center.add(Box.createVerticalStrut(6));

        JButton newOrder = secondaryButton("New order");
        applyDiscountButton = secondaryButton("Apply discount");
        sendKitchenButton = actionButton("Send kitchen", new Color(184, 111, 20));
        readyButton = actionButton("Mark ready", new Color(19, 132, 76));
        momoButton = primaryButton("Pay Momo  (F2)");
        vnpayButton = primaryButton("Pay VNPay");
        JButton receipt = secondaryButton("Receipt preview");

        JPanel discountBlock = new JPanel(new BorderLayout(0, 6));
        discountBlock.setOpaque(false);
        discountBlock.setAlignmentX(Component.LEFT_ALIGNMENT);
        discountBlock.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        discountBlock.add(actionGroupLabel("Discount"), BorderLayout.NORTH);
        JPanel discountRow = new JPanel(new GridLayout(1, 2, 8, 0));
        discountRow.setOpaque(false);
        discountRow.setPreferredSize(new Dimension(0, 36));
        discountRow.add(discountBox);
        discountRow.add(applyDiscountButton);
        discountBlock.add(discountRow, BorderLayout.CENTER);
        center.add(discountBlock);
        center.add(Box.createVerticalStrut(6));

        JPanel orderRows = new JPanel();
        orderRows.setOpaque(false);
        orderRows.setLayout(new BoxLayout(orderRows, BoxLayout.Y_AXIS));
        orderRows.setAlignmentX(Component.LEFT_ALIGNMENT);
        orderRows.setMaximumSize(new Dimension(Integer.MAX_VALUE, 126));
        cancelOrderButton = secondaryButton("Cancel order");
        JPanel row1 = new JPanel(new GridLayout(1, 2, 8, 0));
        row1.setOpaque(false);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        row1.add(newOrder);
        row1.add(cancelOrderButton);
        JPanel row2 = new JPanel(new GridLayout(1, 2, 8, 0));
        row2.setOpaque(false);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        row2.add(sendKitchenButton);
        row2.add(readyButton);
        receipt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        orderRows.add(row1);
        orderRows.add(Box.createVerticalStrut(6));
        orderRows.add(row2);
        orderRows.add(Box.createVerticalStrut(6));
        orderRows.add(receipt);

        JPanel payment = new JPanel(new GridLayout(2, 1, 0, 6));
        payment.setOpaque(false);
        payment.setAlignmentX(Component.LEFT_ALIGNMENT);
        payment.setMaximumSize(new Dimension(Integer.MAX_VALUE, 78));
        payment.add(momoButton);
        payment.add(vnpayButton);

        center.add(actionGroupLabel("Order flow"));
        center.add(Box.createVerticalStrut(4));
        center.add(orderRows);
        center.add(Box.createVerticalStrut(6));
        center.add(actionGroupLabel("Payment"));
        center.add(Box.createVerticalStrut(4));
        center.add(payment);

        paymentStatusLabel.setForeground(new Color(100, 112, 125));
        paymentStatusLabel.setBorder(new EmptyBorder(4, 0, 0, 0));
        paymentStatusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(paymentStatusLabel);

        JScrollPane centerScroll = new JScrollPane(center);
        centerScroll.setBorder(null);
        centerScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        centerScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        centerScroll.getVerticalScrollBar().setUnitIncrement(16);
        centerScroll.getViewport().setBackground(AppTheme.PANEL);
        panel.add(centerScroll, BorderLayout.CENTER);

        newOrder.addActionListener(e -> newOrder());
        cancelOrderButton.addActionListener(e -> changeState(() -> context.orderService.cancel(currentOrder)));
        applyDiscountButton.addActionListener(e -> applyDiscount());
        decreaseQtyButton.addActionListener(e -> changeSelectedItemQuantity(-1));
        increaseQtyButton.addActionListener(e -> changeSelectedItemQuantity(1));
        removeItemButton.addActionListener(e -> removeSelectedBillItem());
        billList.addListSelectionListener(e -> updateActionState());
        sendKitchenButton.addActionListener(e -> changeState(() -> context.orderService.sendToKitchen(currentOrder)));
        readyButton.addActionListener(e -> changeState(() -> context.orderService.markReady(currentOrder)));
        momoButton.addActionListener(e -> pay(new MomoAdapter(new Random(1))));
        vnpayButton.addActionListener(e -> pay(new VnpayAdapter(new Random(1))));
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
        JToggleButton button = AppTheme.toggleButton("<html><b>" + title + "</b><br><span style='font-size:8px'>" + subtitle + "</span></html>");
        button.setPreferredSize(new Dimension(Math.max(70, title.length() * 11 + 28), 40));
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

    private JPanel singleLineHeader(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(t.getFont().deriveFont(Font.BOLD, 18f));
        panel.add(t, BorderLayout.CENTER);
        return panel;
    }

    private JPanel card(LayoutManager layout) {
        return AppTheme.roundedPanel(layout, AppTheme.PANEL, null, 16, new Insets(20, 20, 20, 20));
    }

    private JButton primaryButton(String text) {
        return actionButton(text, AppTheme.ACCENT);
    }

    private JButton secondaryButton(String text) {
        return AppTheme.ghostButton(text);
    }

    private JButton actionButton(String text, Color color) {
        return AppTheme.button(text, color);
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
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "payVnpay");
        root.getActionMap().put("payVnpay", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { pay(new VnpayAdapter(new Random(1))); }
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
        if (selectedMenuItem == null || filtered.stream().noneMatch(item -> item.getId() == selectedMenuItem.getId())) {
            selectedMenuItem = filtered.isEmpty() ? null : filtered.get(0);
        }
        updateSelectedPanel();
        for (MenuItemRecord item : filtered) menuGrid.add(menuCard(item));
        menuGrid.revalidate();
        menuGrid.repaint();
    }

    private void newOrder() {
        currentOrder = context.orderService.createOrder();
        context.orderService.setDiscountStrategy(currentOrder, new NoDiscountStrategy());
        context.orderService.recalculate(currentOrder);
        clearSelectedToppings();
        discountBox.setSelectedItem("No discount");
        paymentStatusLabel.setText("Payment: idle");
        selectedMenuItem = null;
        refreshBill();
        refreshMenu();
    }

    private void updateSelectedPanel() {
        if (selectedMenuItem == null) {
            selectedIconLabel.setIcon(null);
            selectedNameLabel.setText("Select a drink");
            selectedCategoryLabel.setText("Pick from menu");
            selectedPriceLabel.setText(AppTheme.money(0));
            return;
        }
        selectedIconLabel.setIcon(DrinkIconFactory.create(selectedMenuItem.getCategory(), selectedMenuItem.getName(), 112));
        selectedNameLabel.setText(selectedMenuItem.getName());
        selectedCategoryLabel.setText(selectedMenuItem.getCategory());
        selectedPriceLabel.setText(AppTheme.money(selectedMenuItem.getBasePrice()));
    }

    private void addSelectedItem() {
        MenuItemRecord item = selectedMenuItem;
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Please select a beverage first.");
            return;
        }
        try {
            Beverage beverage = context.menuService.createBeverage(item);
            for (Topping topping : selectedToppings()) {
                beverage = context.menuService.applyTopping(beverage, topping.getName());
            }
            context.orderService.addItem(currentOrder, item.getId(), beverage, 1, "");
            refreshBill();
            refreshMenu();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void refreshToppings() {
        activeToppings.clear();
        activeToppings.addAll(context.menuService.getActiveToppings());
        toppingTableModel.setRowCount(0);
        for (Topping topping : activeToppings) {
            toppingTableModel.addRow(new Object[]{false, topping.getName(), AppTheme.money(topping.getExtraPrice())});
        }
    }

    private List<Topping> selectedToppings() {
        List<Topping> selected = new ArrayList<>();
        for (int row = 0; row < toppingTableModel.getRowCount() && row < activeToppings.size(); row++) {
            if (Boolean.TRUE.equals(toppingTableModel.getValueAt(row, 0))) {
                selected.add(activeToppings.get(row));
            }
        }
        return selected;
    }

    private void clearSelectedToppings() {
        for (int row = 0; row < toppingTableModel.getRowCount(); row++) {
            toppingTableModel.setValueAt(false, row, 0);
        }
    }

    private void applyDiscount() {
        context.orderService.setDiscountStrategy(currentOrder, "10%".equals(discountBox.getSelectedItem())
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

    private void refreshOrdersPage() {
        cashierOrderModel.clear();
        context.repository.getOrders().forEach(cashierOrderModel::addElement);
        if (!cashierOrderModel.isEmpty() && cashierOrderList.getSelectedIndex() < 0) {
            cashierOrderList.setSelectedIndex(cashierOrderModel.size() - 1);
        }
        cashierOrderDetailArea.setText(orderDetails(cashierOrderList.getSelectedValue()));
    }

    private void refreshReceiptPage() {
        receiptArea.setText(context.receiptService.buildReceipt(currentOrder));
        receiptArea.setCaretPosition(0);
    }

    private String orderDetails(Order order) {
        if (order == null) return "No orders yet.";
        StringBuilder builder = new StringBuilder();
        builder.append("Order #").append(order.getId()).append("\n");
        builder.append("Status: ").append(order.getStatus()).append("\n");
        builder.append("Created: ").append(order.getCreatedAt()).append("\n\n");
        if (order.getItems().isEmpty()) {
            builder.append("No items.\n");
        } else {
            for (OrderItem item : order.getItems()) {
                builder.append("- ").append(item).append("\n");
            }
        }
        builder.append("\nSubtotal: ").append(AppTheme.money(order.getSubtotal()));
        builder.append("\nDiscount: -").append(AppTheme.money(order.getDiscountAmount()));
        builder.append("\nTotal: ").append(AppTheme.money(order.getTotalAmount()));
        if (order.getPayment() != null) {
            builder.append("\nPayment: ").append(order.getPayment().getMethod())
                    .append(" / ").append(order.getPayment().getTransactionCode());
        }
        return builder.toString();
    }

    private void changeState(Runnable action) {
        try {
            action.run();
            refreshBill();
            refreshOrdersPage();
            refreshReceiptPage();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void changeSelectedItemQuantity(int delta) {
        OrderItem item = selectedBillItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Please select an item in the cart first.");
            return;
        }
        try {
            context.orderService.updateItemQuantity(currentOrder, item.getId(), item.getQuantity() + delta);
            refreshBill();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void removeSelectedBillItem() {
        OrderItem item = selectedBillItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Please select an item in the cart first.");
            return;
        }
        try {
            context.orderService.removeItem(currentOrder, item.getId());
            refreshBill();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private OrderItem selectedBillItem() {
        int index = billList.getSelectedIndex();
        if (index < 0 || index >= billItems.size()) return null;
        return billItems.get(index);
    }

    private void refreshBill() {
        billModel.clear();
        billItems.clear();
        if (currentOrder.getItems().isEmpty()) {
            billModel.addElement("No items yet");
            billModel.addElement("Select a drink card from the menu.");
        } else {
            for (OrderItem item : currentOrder.getItems()) {
                billItems.add(item);
                billModel.addElement(item.toString());
            }
        }
        orderStatusLabel.setText("Order #" + currentOrder.getId() + " - " + currentOrder.getStatus());
        subtotalLabel.setText(AppTheme.money(currentOrder.getSubtotal()));
        discountLabel.setText("-" + AppTheme.money(currentOrder.getDiscountAmount()));
        totalLabel.setText(AppTheme.money(currentOrder.getTotalAmount()));
        updateActionState();
        refreshReceiptPage();
    }

    private void updateActionState() {
        boolean pending = "PENDING".equals(currentOrder.getStatus());
        boolean preparing = "PREPARING".equals(currentOrder.getStatus());
        boolean ready = "READY".equals(currentOrder.getStatus());
        boolean hasItems = !currentOrder.getItems().isEmpty();
        boolean itemSelected = selectedBillItem() != null;

        if (decreaseQtyButton != null) decreaseQtyButton.setEnabled(pending && itemSelected);
        if (increaseQtyButton != null) increaseQtyButton.setEnabled(pending && itemSelected);
        if (removeItemButton != null) removeItemButton.setEnabled(pending && itemSelected);
        if (applyDiscountButton != null) applyDiscountButton.setEnabled(pending && hasItems);
        if (sendKitchenButton != null) sendKitchenButton.setEnabled(pending && hasItems);
        if (readyButton != null) readyButton.setEnabled(preparing);
        if (momoButton != null) momoButton.setEnabled(ready);
        if (vnpayButton != null) vnpayButton.setEnabled(ready);
        if (cancelOrderButton != null) cancelOrderButton.setEnabled((pending || preparing || ready) && !"PAID".equals(currentOrder.getStatus()));
    }

    private JPanel menuCard(MenuItemRecord item) {
        boolean selected = selectedMenuItem != null && selectedMenuItem.getId() == item.getId();
        JPanel card = AppTheme.roundedPanel(new BorderLayout(8, 8),
                selected ? new Color(255, 245, 235) : AppTheme.PANEL,
                selected ? AppTheme.ACCENT : null,
                16, new Insets(16, 16, 16, 16));
        card.setPreferredSize(new Dimension(168, 196));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel icon = new JLabel(DrinkIconFactory.create(item.getCategory(), item.getName(), 62));
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

        JPanel bottom = new JPanel(new BorderLayout(8, 6));
        bottom.setOpaque(false);
        bottom.add(text, BorderLayout.CENTER);
        if (selected) {
            JLabel selectedBadge = new JLabel("Selected");
            selectedBadge.setForeground(AppTheme.ACCENT);
            selectedBadge.setFont(selectedBadge.getFont().deriveFont(Font.BOLD, 11f));
            bottom.add(selectedBadge, BorderLayout.SOUTH);
        }
        card.add(bottom, BorderLayout.CENTER);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectedMenuItem = item;
                updateSelectedPanel();
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
            label.setOpaque(true);
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, selected ? 4 : 0, 0, 0, selected ? AppTheme.PRIMARY : AppTheme.BORDER),
                    new EmptyBorder(8, selected ? 8 : 10, 8, 10)
            ));
            label.setBackground(selected ? new Color(255, 237, 213) : (index % 2 == 0 ? AppTheme.SURFACE : Color.WHITE));
            label.setForeground(AppTheme.TEXT);
            if (selected) {
                label.setFont(label.getFont().deriveFont(Font.BOLD));
            }
            return label;
        }
    }

    private static class OrderListRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
            Order order = (Order) value;
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
            label.setText("<html><b>Order #" + order.getId() + "</b> - " + order.getStatus()
                    + "<br>" + order.getItems().size() + " line(s) - " + AppTheme.money(order.getTotalAmount()) + "</html>");
            label.setBorder(new EmptyBorder(10, 12, 10, 12));
            label.setBackground(selected ? new Color(255, 241, 225) : (index % 2 == 0 ? AppTheme.SURFACE : Color.WHITE));
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
