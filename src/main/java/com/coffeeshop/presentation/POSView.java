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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class POSView extends JFrame {
    private final AppContext context;
    private Order currentOrder;
    private final CardLayout workspaceLayout = new CardLayout();
    private final JPanel workspacePanel = new JPanel(workspaceLayout);
    private JPanel selectedPanel;
    private final DefaultListModel<String> billModel = new DefaultListModel<>();
    private final JList<String> billList = new JList<>(billModel);
    private final List<OrderItem> billItems = new ArrayList<>();
    private final DefaultListModel<Order> cashierOrderModel = new DefaultListModel<>();
    private final JList<Order> cashierOrderList = new JList<>(cashierOrderModel);
    private final JTextArea cashierOrderDetailArea = new JTextArea();
    private final JTextArea receiptArea = new JTextArea();
    private final JPanel menuGrid = new JPanel(new WrapLayout(FlowLayout.LEFT, 24, 24));
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
    private final JLabel cartMetaLabel = new JLabel();
    private final JLabel subtotalLabel = new JLabel();
    private final JLabel discountLabel = new JLabel();
    private final JLabel totalLabel = new JLabel();
    private final JLabel paymentStatusLabel = new JLabel("Payment: idle");
    private final JLabel selectedIconLabel = new JLabel();
    private final JLabel selectedNameLabel = new JLabel("Select a drink");
    private final JLabel selectedCategoryLabel = new JLabel("Pick from menu");
    private final JLabel selectedToppingsLabel = new JLabel();
    private final JLabel selectedPriceLabel = new JLabel(AppTheme.money(0));
    private final JLabel addStatusLabel = new JLabel(" ");
    private Timer addStatusTimer;
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
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = Math.min(1480, Math.max(1220, screen.width - 72));
        int height = Math.min(900, Math.max(720, screen.height - 88));
        setTitle("Coffee Shop POS - Cashier");
        setSize(width, height);
        setMinimumSize(new Dimension(1180, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(AppShell.wrap(this, context, "Cashier",
                "",
                buildWorkspace(), this::showCashierPage, "POS", "Cart", "Orders", "Receipt"));
        installShortcuts();
        refreshMenu();
        refreshBill();
    }

    private JComponent buildWorkspace() {
        workspacePanel.setOpaque(false);
        workspacePanel.add(buildPosPage(), "POS");
        workspacePanel.add(buildCartPage(), "Cart");
        workspacePanel.add(buildOrdersPage(), "Orders");
        workspacePanel.add(buildReceiptPage(), "Receipt");
        return workspacePanel;
    }

    private void showCashierPage(String page) {
        if ("Cart".equals(page)) refreshBill();
        if ("Orders".equals(page)) refreshOrdersPage();
        if ("Receipt".equals(page)) refreshReceiptPage();
        workspaceLayout.show(workspacePanel, page);
    }

    private JComponent buildPosPage() {
        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);
        selectedPanel = buildSelectedPanel();
        root.add(split(buildMenuPanel(), selectedPanel, 760, 0.68), BorderLayout.CENTER);
        return root;
    }

    private JComponent buildCartPage() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setOpaque(false);
        root.add(buildCartPanel(), BorderLayout.CENTER);
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
        cashierOrderDetailArea.setLineWrap(true);
        cashierOrderDetailArea.setWrapStyleWord(true);
        cashierOrderDetailArea.setBackground(AppTheme.SURFACE);
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
        receiptArea.setBackground(AppTheme.SURFACE);
        panel.add(new JScrollPane(receiptArea), BorderLayout.CENTER);
        preview.addActionListener(e -> showReceipt());
        refreshReceiptPage();
        return panel;
    }

    private JPanel buildSelectedPanel() {
        JPanel panel = AppTheme.roundedPanel(new BorderLayout(0, 24),
                AppTheme.DETAIL_PANEL, AppTheme.DETAIL_BORDER, 18, new Insets(28, 28, 28, 28));
        panel.setPreferredSize(new Dimension(344, 0));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        JLabel eyebrow = new JLabel("CURRENT DRINK");
        eyebrow.setForeground(AppTheme.PRIMARY);
        eyebrow.setFont(eyebrow.getFont().deriveFont(Font.BOLD, 11f));
        selectedNameLabel.setForeground(AppTheme.TEXT);
        selectedNameLabel.setFont(selectedNameLabel.getFont().deriveFont(Font.BOLD, 26f));
        selectedCategoryLabel.setForeground(AppTheme.MUTED);
        selectedCategoryLabel.setFont(selectedCategoryLabel.getFont().deriveFont(Font.PLAIN, 14f));
        top.add(eyebrow);
        top.add(Box.createVerticalStrut(8));
        top.add(selectedNameLabel);
        top.add(Box.createVerticalStrut(4));
        top.add(selectedCategoryLabel);
        addStatusLabel.setForeground(AppTheme.SUCCESS);
        addStatusLabel.setFont(addStatusLabel.getFont().deriveFont(Font.BOLD, 12f));
        top.add(Box.createVerticalStrut(6));
        top.add(addStatusLabel);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        selectedIconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        selectedIconLabel.setVerticalAlignment(SwingConstants.TOP);
        selectedIconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectedIconLabel.setPreferredSize(new Dimension(200, 144));
        selectedIconLabel.setMinimumSize(new Dimension(200, 128));
        selectedIconLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 144));
        selectedPriceLabel.setForeground(AppTheme.PRIMARY.darker());
        selectedPriceLabel.setFont(selectedPriceLabel.getFont().deriveFont(Font.BOLD, 30f));
        selectedPriceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        selectedPriceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectedToppingsLabel.setForeground(AppTheme.MUTED);
        selectedToppingsLabel.setFont(selectedToppingsLabel.getFont().deriveFont(Font.PLAIN, 13f));
        selectedToppingsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        selectedToppingsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel summary = new JPanel();
        summary.setOpaque(false);
        summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));
        summary.setAlignmentX(Component.CENTER_ALIGNMENT);
        summary.add(selectedPriceLabel);
        summary.add(Box.createVerticalStrut(10));
        summary.add(selectedToppingsLabel);
        center.add(Box.createVerticalStrut(4));
        center.add(selectedIconLabel);
        center.add(Box.createVerticalStrut(8));
        center.add(summary);

        JPanel bottom = new JPanel(new BorderLayout(0, 16));
        bottom.setOpaque(false);

        toppingTable.setRowHeight(38);
        toppingTable.setShowGrid(false);
        toppingTable.setIntercellSpacing(new Dimension(0, 0));
        toppingTable.setSelectionBackground(AppTheme.TINT);
        toppingTable.setSelectionForeground(AppTheme.TEXT);
        toppingTable.setForeground(AppTheme.TEXT);
        toppingTable.setBackground(AppTheme.SURFACE);
        toppingTable.setTableHeader(null);
        toppingTable.setFont(toppingTable.getFont().deriveFont(13f));
        if (toppingTableModel.getTableModelListeners().length == 0) {
            toppingTableModel.addTableModelListener(e -> updateSelectedPanel());
        }

        TableColumnModel columns = toppingTable.getColumnModel();
        columns.getColumn(0).setPreferredWidth(24);
        columns.getColumn(0).setMaxWidth(28);
        columns.getColumn(1).setPreferredWidth(170);
        columns.getColumn(2).setPreferredWidth(86);

        JScrollPane scroll = new JScrollPane(toppingTable);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        scroll.getViewport().setBackground(AppTheme.SURFACE);
        scroll.setPreferredSize(new Dimension(280, 164));

        JPanel toppingBlock = new JPanel(new BorderLayout(0, 10));
        toppingBlock.setOpaque(false);
        JLabel toppingTitle = new JLabel("TOPPINGS");
        toppingTitle.setForeground(AppTheme.TEXT);
        toppingTitle.setFont(toppingTitle.getFont().deriveFont(Font.BOLD, 12f));
        toppingBlock.add(toppingTitle, BorderLayout.NORTH);
        toppingBlock.add(scroll, BorderLayout.CENTER);
        bottom.add(toppingBlock, BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(2, 1, 0, 10));
        actions.setOpaque(false);
        JButton add = AppTheme.button("Add to cart (F1)", AppTheme.PRIMARY);
        add.setPreferredSize(new Dimension(0, 48));
        add.addActionListener(e -> addSelectedItem());
        JButton viewCart = AppTheme.ghostButton("Review cart and payment");
        viewCart.addActionListener(e -> showCashierPage("Cart"));
        actions.add(add);
        actions.add(viewCart);
        bottom.add(actions, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        updateSelectedPanel();
        refreshToppings();
        return panel;
    }

    private JPanel buildMenuPanel() {
        JPanel panel = card(new BorderLayout(28, 24));
        panel.setMinimumSize(new Dimension(560, 0));
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(sectionHeader("Menu", "Browse the drinks, then fine-tune the order in the detail panel."), BorderLayout.WEST);
        orderStatusLabel.setFont(orderStatusLabel.getFont().deriveFont(Font.BOLD, 14f));
        orderStatusLabel.setForeground(AppTheme.WARNING.darker());
        top.add(orderStatusLabel, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        JPanel filters = new JPanel(new BorderLayout(8, 8));
        filters.setOpaque(false);
        AppTheme.styleField(searchField);
        searchField.setToolTipText("Search drinks by name");
        JButton refresh = secondaryButton("Refresh menu");
        JPanel right = new JPanel(new BorderLayout(8, 8));
        right.setOpaque(false);
        right.add(refresh, BorderLayout.EAST);
        filters.add(searchField, BorderLayout.CENTER);
        filters.add(right, BorderLayout.EAST);

        JPanel categories = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        categories.setOpaque(false);
        categories.setPreferredSize(new Dimension(0, 50));
        addCategoryButton(categories, "ALL", "All", countMenuItems("ALL") + " items");
        addCategoryButton(categories, "COFFEE", "Coffee", countMenuItems("COFFEE") + " items");
        addCategoryButton(categories, "TEA", "Tea", countMenuItems("TEA") + " items");
        addCategoryButton(categories, "MATCHA", "Matcha", countMenuItems("MATCHA") + " items");
        addCategoryButton(categories, "SMOOTHIE", "Smoothie", countMenuItems("SMOOTHIE") + " items");

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



    private JPanel buildCartPanel() {
        JPanel panel = card(new BorderLayout(0, 24));
        panel.setMinimumSize(new Dimension(840, 0));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel cartTitle = new JLabel("Cart & Checkout");
        cartTitle.setForeground(AppTheme.TEXT);
        cartTitle.setFont(cartTitle.getFont().deriveFont(Font.BOLD, 22f));
        JPanel titleStack = new JPanel();
        titleStack.setOpaque(false);
        titleStack.setLayout(new BoxLayout(titleStack, BoxLayout.Y_AXIS));
        titleStack.add(cartTitle);
        cartMetaLabel.setForeground(AppTheme.MUTED);
        cartMetaLabel.setFont(cartMetaLabel.getFont().deriveFont(Font.PLAIN, 12f));
        titleStack.add(Box.createVerticalStrut(4));
        titleStack.add(cartMetaLabel);
        top.add(titleStack, BorderLayout.WEST);
        panel.add(top, BorderLayout.NORTH);

        billList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        billList.setCellRenderer(new BillRenderer());
        JScrollPane billScroll = new JScrollPane(billList);
        billScroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        billScroll.setPreferredSize(new Dimension(0, 420));
        billScroll.getViewport().setBackground(AppTheme.SURFACE);
        billScroll.getVerticalScrollBar().setUnitIncrement(18);

        JPanel itemActions = new JPanel(new GridLayout(1, 3, 8, 0));
        itemActions.setOpaque(false);
        decreaseQtyButton = compactButton("- 1");
        increaseQtyButton = compactButton("+ 1");
        removeItemButton = compactButton("Remove");
        itemActions.add(decreaseQtyButton);
        itemActions.add(increaseQtyButton);
        itemActions.add(removeItemButton);

        JPanel itemColumn = AppTheme.roundedPanel(new BorderLayout(0, 18),
                AppTheme.SURFACE, AppTheme.BORDER, 18, new Insets(24, 24, 24, 24));
        itemColumn.add(sectionHeader("Items", "Select a line item to adjust quantity or remove it."), BorderLayout.NORTH);
        itemColumn.add(billScroll, BorderLayout.CENTER);
        JPanel itemFooter = new JPanel(new BorderLayout(0, 10));
        itemFooter.setOpaque(false);
        itemFooter.add(itemActions, BorderLayout.NORTH);
        JLabel helper = AppTheme.muted("Tip: double-click a drink on the POS page to add it quickly.");
        helper.setBorder(new EmptyBorder(2, 2, 0, 0));
        itemFooter.add(helper, BorderLayout.SOUTH);
        itemColumn.add(itemFooter, BorderLayout.SOUTH);

        JPanel summary = new JPanel(new GridLayout(3, 1, 0, 8));
        summary.setOpaque(false);
        summary.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 0, AppTheme.BORDER),
                new EmptyBorder(10, 0, 10, 0)
        ));
        summary.add(summaryLine("Subtotal", subtotalLabel));
        summary.add(summaryLine("Discount", discountLabel));
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 20f));
        totalLabel.setForeground(AppTheme.ACCENT);
        summary.add(summaryLine("Total", totalLabel));

        JButton newOrder = compactButton("New order");
        applyDiscountButton = compactButton("Apply");
        sendKitchenButton = compactActionButton("Send kitchen", AppTheme.PRIMARY);
        readyButton = compactActionButton("Ready", AppTheme.SUCCESS);
        momoButton = compactActionButton("Pay Momo", AppTheme.ACCENT);
        vnpayButton = compactActionButton("Pay VNPay", AppTheme.ACCENT);
        JButton receipt = compactButton("Receipt");

        JPanel summaryCard = AppTheme.roundedPanel(new BorderLayout(0, 14),
                AppTheme.PANEL, AppTheme.BORDER, 18, new Insets(20, 20, 20, 20));
        summaryCard.add(sectionHeader("Summary", "Live totals update as you edit the order."), BorderLayout.NORTH);
        summaryCard.add(summary, BorderLayout.CENTER);

        JPanel discountBlock = AppTheme.roundedPanel(new BorderLayout(0, 10),
                AppTheme.PANEL, AppTheme.BORDER, 18, new Insets(18, 18, 18, 18));
        discountBlock.add(singleLineHeader("Discount"), BorderLayout.NORTH);
        JPanel discountRow = new JPanel(new BorderLayout(8, 0));
        discountRow.setOpaque(false);
        discountBox.setPreferredSize(new Dimension(150, 36));
        discountRow.add(discountBox, BorderLayout.CENTER);
        discountRow.add(applyDiscountButton, BorderLayout.EAST);
        discountBlock.add(discountRow, BorderLayout.CENTER);

        JPanel orderRows = new JPanel();
        orderRows.setOpaque(false);
        orderRows.setLayout(new BoxLayout(orderRows, BoxLayout.Y_AXIS));
        cancelOrderButton = compactButton("Cancel");
        JPanel row1 = new JPanel(new GridLayout(1, 2, 8, 0));
        row1.setOpaque(false);
        row1.add(newOrder);
        row1.add(cancelOrderButton);
        JPanel row2 = new JPanel(new GridLayout(1, 2, 8, 0));
        row2.setOpaque(false);
        row2.add(sendKitchenButton);
        row2.add(readyButton);
        orderRows.add(row1);
        orderRows.add(Box.createVerticalStrut(6));
        orderRows.add(row2);
        orderRows.add(Box.createVerticalStrut(6));
        orderRows.add(receipt);

        JPanel orderCard = AppTheme.roundedPanel(new BorderLayout(0, 14),
                AppTheme.PANEL, AppTheme.BORDER, 18, new Insets(18, 18, 18, 18));
        orderCard.add(sectionHeader("Order flow", "Move the ticket from pending to ready."), BorderLayout.NORTH);
        orderCard.add(orderRows, BorderLayout.CENTER);

        JPanel payment = new JPanel(new GridLayout(1, 2, 8, 0));
        payment.setOpaque(false);
        payment.add(momoButton);
        payment.add(vnpayButton);

        JPanel paymentCard = AppTheme.roundedPanel(new BorderLayout(0, 14),
                AppTheme.PANEL, AppTheme.BORDER, 18, new Insets(18, 18, 18, 18));
        paymentCard.add(sectionHeader("Payment", "Enable checkout when the order is marked ready."), BorderLayout.NORTH);
        paymentStatusLabel.setForeground(AppTheme.MUTED);
        paymentStatusLabel.setBorder(new EmptyBorder(2, 0, 0, 0));
        paymentCard.add(payment, BorderLayout.CENTER);
        paymentCard.add(paymentStatusLabel, BorderLayout.SOUTH);

        JPanel checkoutColumn = new JPanel();
        checkoutColumn.setOpaque(false);
        checkoutColumn.setLayout(new BoxLayout(checkoutColumn, BoxLayout.Y_AXIS));
        summaryCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        discountBlock.setAlignmentX(Component.LEFT_ALIGNMENT);
        orderCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        paymentCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkoutColumn.add(summaryCard);
        checkoutColumn.add(Box.createVerticalStrut(14));
        checkoutColumn.add(discountBlock);
        checkoutColumn.add(Box.createVerticalStrut(14));
        checkoutColumn.add(orderCard);
        checkoutColumn.add(Box.createVerticalStrut(14));
        checkoutColumn.add(paymentCard);
        checkoutColumn.add(Box.createVerticalGlue());

        JScrollPane checkoutScroll = new JScrollPane(checkoutColumn);
        checkoutScroll.setBorder(null);
        checkoutScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        checkoutScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        checkoutScroll.getVerticalScrollBar().setUnitIncrement(16);
        checkoutScroll.getViewport().setBackground(AppTheme.PANEL);
        panel.add(split(itemColumn, checkoutScroll, 670, 0.62), BorderLayout.CENTER);

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
        button.setPreferredSize(new Dimension(Math.max(88, title.length() * 11 + 30), 40));
        button.addActionListener(e -> {
            selectedCategory = key;
            refreshMenu();
        });
        categoryGroup.add(button);
        parent.add(button);
        if ("ALL".equals(key)) button.setSelected(true);
    }

    private int countMenuItems(String category) {
        return (int) context.menuService.getActiveMenu().stream()
                .filter(item -> "ALL".equals(category) || category.equals(item.getCategory()))
                .count();
    }

    private String statusText(String status) {
        return switch (status) {
            case "PENDING" -> "Pending";
            case "PREPARING" -> "Preparing";
            case "READY" -> "Ready";
            case "PAID" -> "Paid";
            case "CANCELLED" -> "Cancelled";
            default -> status;
        };
    }

    private Color statusColor(String status) {
        return switch (status) {
            case "PENDING" -> AppTheme.WARNING;
            case "PREPARING" -> AppTheme.PRIMARY;
            case "READY" -> AppTheme.SUCCESS;
            case "PAID" -> AppTheme.ACCENT;
            case "CANCELLED" -> AppTheme.DANGER;
            default -> AppTheme.MUTED;
        };
    }

    private String categoryText(String category) {
        return switch (category) {
            case "COFFEE" -> "Coffee";
            case "TEA" -> "Tea";
            case "MATCHA" -> "Matcha";
            case "SMOOTHIE" -> "Smoothie";
            default -> category;
        };
    }

    private String paymentMethodText(String method) {
        return switch (method == null ? "" : method.toUpperCase()) {
            case "MOMO" -> "Momo";
            case "VNPAY" -> "VNPay";
            default -> method == null ? "Unknown" : method;
        };
    }

    private JPanel sectionHeader(String title, String subtitle) {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 2));
        panel.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(t.getFont().deriveFont(Font.BOLD, 18f));
        JLabel s = new JLabel(subtitle);
        s.setForeground(AppTheme.MUTED);
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

    private JButton compactButton(String text) {
        JButton button = secondaryButton(text);
        compact(button);
        return button;
    }

    private JButton compactActionButton(String text, Color color) {
        JButton button = actionButton(text, color);
        compact(button);
        return button;
    }

    private void compact(JButton button) {
        button.setFont(button.getFont().deriveFont(Font.BOLD, 12f));
        button.setBorder(new EmptyBorder(7, 10, 7, 10));
        button.setMinimumSize(new Dimension(90, 32));
        button.setPreferredSize(new Dimension(118, 34));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
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
        if (filtered.isEmpty()) {
            menuGrid.add(emptyMenuState(query.isEmpty()
                    ? "No drinks are available in this category yet."
                    : "No drinks match your search."));
        } else {
            for (MenuItemRecord item : filtered) menuGrid.add(menuCard(item));
        }
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

    private void resetCurrentDrinkSelection() {
        selectedMenuItem = null;
        clearSelectedToppings();
        addStatusLabel.setText(" ");
        if (addStatusTimer != null && addStatusTimer.isRunning()) {
            addStatusTimer.stop();
        }
        updateSelectedPanel();
    }

    private void updateSelectedPanel() {
        if (selectedMenuItem == null) {
            selectedIconLabel.setIcon(null);
            selectedNameLabel.setText("Select a drink");
            selectedCategoryLabel.setText("Pick from menu");
            selectedToppingsLabel.setText("No topping selected");
            selectedPriceLabel.setText(AppTheme.money(0));
            return;
        }
        List<Topping> toppings = selectedToppings();
        double total = selectedMenuItem.getBasePrice() + toppings.stream().mapToDouble(Topping::getExtraPrice).sum();
        selectedIconLabel.setIcon(DrinkIconFactory.create(selectedMenuItem.getCategory(), selectedMenuItem.getName(), 112));
        selectedNameLabel.setText(selectedMenuItem.getName());
        selectedCategoryLabel.setText(categoryText(selectedMenuItem.getCategory()));
        selectedToppingsLabel.setText(toppings.isEmpty()
                ? "No topping selected"
                : "<html>Toppings<br>" + toppingSummary(toppings) + "</html>");
        selectedPriceLabel.setText(AppTheme.money(total));
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
            refreshToppings();
            showAddStatus("Added to cart successfully");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void showAddStatus(String message) {
        addStatusLabel.setText(message);
        if (addStatusTimer != null && addStatusTimer.isRunning()) {
            addStatusTimer.stop();
        }
        addStatusTimer = new Timer(2000, e -> addStatusLabel.setText(" "));
        addStatusTimer.setRepeats(false);
        addStatusTimer.start();
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

    private String toppingSummary(List<Topping> toppings) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < toppings.size(); i++) {
            if (i > 0) builder.append("<br>");
            Topping topping = toppings.get(i);
            builder.append(topping.getName()).append(" (+").append(AppTheme.money(topping.getExtraPrice())).append(")");
        }
        return builder.toString();
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
        paymentStatusLabel.setText("Payment: processing " + paymentMethodText(gateway.getGatewayName()) + "...");
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
        builder.append("Status     : ").append(statusText(order.getStatus())).append("\n");
        builder.append("Created    : ").append(order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        builder.append("Items      : ").append(order.getItems().size()).append("\n");
        builder.append("\nOrder lines\n");
        builder.append("------------------------------\n");
        if (order.getItems().isEmpty()) {
            builder.append("No items.\n");
        } else {
            for (OrderItem item : order.getItems()) {
                builder.append("- ").append(item.getQuantity())
                        .append(" x ").append(item.getBeverage().getDescription())
                        .append("  |  ").append(AppTheme.money(item.getItemPrice()))
                        .append("\n");
            }
        }
        builder.append("\nSummary\n");
        builder.append("------------------------------\n");
        builder.append("Subtotal  : ").append(AppTheme.money(order.getSubtotal()));
        builder.append("\nDiscount  : -").append(AppTheme.money(order.getDiscountAmount()));
        builder.append("\nTotal     : ").append(AppTheme.money(order.getTotalAmount()));
        if (order.getPayment() != null) {
            builder.append("\nPayment   : ").append(paymentMethodText(order.getPayment().getMethod()))
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
            billModel.addElement("empty-1");
            billModel.addElement("empty-2");
        } else {
            for (OrderItem item : currentOrder.getItems()) {
                billItems.add(item);
                billModel.addElement(item.toString());
            }
        }
        orderStatusLabel.setText("Order #" + currentOrder.getId() + " - " + statusText(currentOrder.getStatus()));
        orderStatusLabel.setForeground(statusColor(currentOrder.getStatus()));
        cartMetaLabel.setText(currentOrder.getItems().isEmpty()
                ? "Order #" + currentOrder.getId() + " - " + statusText(currentOrder.getStatus()) + " - no items yet."
                : "Order #" + currentOrder.getId() + " - " + statusText(currentOrder.getStatus())
                + " - " + currentOrder.getItems().size() + " items - " + AppTheme.money(currentOrder.getTotalAmount()));
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
                selected ? AppTheme.DETAIL_PANEL : AppTheme.PANEL,
                selected ? AppTheme.PRIMARY : null,
                18, new Insets(18, 18, 18, 18));
        card.setPreferredSize(new Dimension(184, 214));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel icon = new JLabel(DrinkIconFactory.create(item.getCategory(), item.getName(), 68));
        icon.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(icon, BorderLayout.NORTH);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel name = new JLabel(item.getName());
        name.setForeground(AppTheme.TEXT);
        name.setFont(name.getFont().deriveFont(Font.BOLD, 16f));
        JLabel category = AppTheme.muted(categoryText(item.getCategory()));
        JLabel price = new JLabel(AppTheme.money(item.getBasePrice()));
        price.setForeground(AppTheme.PRIMARY);
        price.setFont(price.getFont().deriveFont(Font.BOLD, 16f));
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
            selectedBadge.setForeground(AppTheme.PRIMARY.darker());
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

    private JPanel emptyMenuState(String message) {
        JPanel state = AppTheme.roundedPanel(new BorderLayout(), AppTheme.SURFACE, AppTheme.BORDER, 16, new Insets(22, 22, 22, 22));
        state.setPreferredSize(new Dimension(220, 140));
        JLabel label = new JLabel("<html><div style='text-align:center'>" + message + "</div></html>");
        label.setForeground(AppTheme.MUTED);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        state.add(label, BorderLayout.CENTER);
        return state;
    }

    private class BillRenderer implements ListCellRenderer<String> {
        public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean selected, boolean focus) {
            if (billItems.isEmpty()) {
                JLabel empty = new JLabel(index == 0 ? "No items in the cart yet." : "Choose a drink from the menu to get started.");
                empty.setOpaque(true);
                empty.setForeground(index == 0 ? AppTheme.TEXT : AppTheme.MUTED);
                empty.setBackground(index == 0 ? AppTheme.SURFACE : AppTheme.PANEL);
                empty.setBorder(new EmptyBorder(index == 0 ? 18 : 8, 14, index == 0 ? 8 : 18, 14));
                return empty;
            }

            OrderItem item = billItems.get(index);
            JPanel row = new JPanel(new BorderLayout(12, 4));
            row.setOpaque(true);
            row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, selected ? 4 : 0, 0, 0, selected ? AppTheme.PRIMARY : AppTheme.BORDER),
                    new EmptyBorder(10, selected ? 10 : 14, 10, 14)
            ));
            row.setBackground(selected ? AppTheme.TINT : (index % 2 == 0 ? AppTheme.SURFACE : AppTheme.PANEL));

            JPanel text = new JPanel();
            text.setOpaque(false);
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

            JLabel name = new JLabel(item.getBeverage().getDescription());
            name.setForeground(AppTheme.TEXT);
            name.setFont(name.getFont().deriveFont(Font.BOLD, 13f));
            JLabel meta = AppTheme.muted("Qty: " + item.getQuantity());
            text.add(name);
            text.add(Box.createVerticalStrut(4));
            text.add(meta);

            JLabel price = new JLabel(AppTheme.money(item.getItemPrice()));
            price.setForeground(AppTheme.PRIMARY);
            price.setFont(price.getFont().deriveFont(Font.BOLD, 13f));

            row.add(text, BorderLayout.CENTER);
            row.add(price, BorderLayout.EAST);
            return row;
        }
    }

    private JSplitPane split(Component left, Component right, int dividerLocation, double resizeWeight) {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setOpaque(false);
        split.setBorder(null);
        split.setDividerSize(10);
        split.setContinuousLayout(true);
        split.setOneTouchExpandable(false);
        split.setDividerLocation(dividerLocation);
        split.setResizeWeight(resizeWeight);
        split.setBackground(AppTheme.BG);
        return split;
    }

    private class OrderListRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
            Order order = (Order) value;
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
            label.setText("<html><b>Order #" + order.getId() + "</b> - " + statusText(order.getStatus())
                    + "<br>" + order.getItems().size() + " item(s) - " + AppTheme.money(order.getTotalAmount()) + "</html>");
            label.setBorder(new EmptyBorder(10, 12, 10, 12));
            label.setBackground(selected ? AppTheme.TINT : (index % 2 == 0 ? AppTheme.SURFACE : AppTheme.PANEL));
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
