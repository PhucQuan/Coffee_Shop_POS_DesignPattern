package com.coffeeshop.presentation;

import com.coffeeshop.AppContext;
import com.coffeeshop.domain.model.InventoryItem;
import com.coffeeshop.domain.model.Order;
import com.coffeeshop.domain.model.OrderItem;
import com.coffeeshop.domain.model.RecipeItem;
import com.coffeeshop.domain.model.Topping;
import com.coffeeshop.domain.model.User;
import com.coffeeshop.infrastructure.InventoryTransactionRecord;
import com.coffeeshop.infrastructure.MenuItemRecord;
import com.coffeeshop.infrastructure.OrderStatusHistoryRecord;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class AdminView extends JFrame {
    private final AppContext context;
    private final CardLayout adminLayout = new CardLayout();
    private final JPanel adminPages = new JPanel(adminLayout);
    private final JTextArea reportArea = new JTextArea();
    private final BarChartPanel menuOverviewChart = new BarChartPanel();
    private final BarChartPanel toppingOverviewChart = new BarChartPanel();
    private final JLabel totalOrdersMetric = new JLabel("0");
    private final JLabel paidOrdersMetric = new JLabel("0");
    private final JLabel revenueMetric = new JLabel("0 d");
    private final JLabel revenueCard = new JLabel("Revenue: 0d");
    private final DefaultTableModel menuReportModel = new DefaultTableModel(new Object[]{"Menu", "Qty"}, 0) {
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final DefaultTableModel toppingReportModel = new DefaultTableModel(new Object[]{"Topping", "Qty"}, 0) {
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final DefaultListModel<MenuItemRecord> menuModel = new DefaultListModel<>();
    private final DefaultListModel<Topping> toppingModel = new DefaultListModel<>();
    private final DefaultListModel<InventoryItem> inventoryModel = new DefaultListModel<>();
    private final DefaultListModel<RecipeRow> recipeModel = new DefaultListModel<>();
    private final DefaultListModel<Order> activeOrderModel = new DefaultListModel<>();
    private final DefaultListModel<Order> historyOrderModel = new DefaultListModel<>();
    private final DefaultListModel<User> userModel = new DefaultListModel<>();
    private final JList<MenuItemRecord> menuList = new JList<>(menuModel);
    private final JList<Topping> toppingList = new JList<>(toppingModel);
    private final JList<RecipeRow> recipeList = new JList<>(recipeModel);
    private final JList<Order> activeOrderList = new JList<>(activeOrderModel);
    private final JList<Order> historyOrderList = new JList<>(historyOrderModel);
    private final JList<User> userList = new JList<>(userModel);
    private final JTextArea activeOrderDetailArea = new JTextArea();
    private final JTextArea historyOrderDetailArea = new JTextArea();
    private final JTextArea statusHistoryArea = new JTextArea();
    private final JTextArea inventoryTransactionArea = new JTextArea();
    private final JTextArea operationsSummaryArea = new JTextArea();
    private final JTextField beverageNameField = new JTextField();
    private final JTextField beveragePriceField = new JTextField();
    private final JComboBox<String> categoryBox = new JComboBox<>(new String[]{"COFFEE", "TEA", "MATCHA", "SMOOTHIE"});
    private final JCheckBox beverageActiveBox = new JCheckBox("Active", true);
    private final JComboBox<InventoryItem> recipeInventoryBox = new JComboBox<>();
    private final JTextField recipeQuantityField = new JTextField();
    private final JTextField toppingNameField = new JTextField();
    private final JTextField toppingPriceField = new JTextField();
    private final JCheckBox toppingActiveBox = new JCheckBox("Active", true);
    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JComboBox<String> roleBox = new JComboBox<>(new String[]{"ADMIN", "CASHIER", "KITCHEN"});

    public AdminView(AppContext context) {
        this.context = context;
        setTitle("Coffee Shop POS - Admin");
        setSize(1180, 720);
        setMinimumSize(new Dimension(1040, 640));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        adminPages.setOpaque(false);
        adminPages.add(overviewPanel(), "Overview");
        adminPages.add(ordersPanel(), "Orders");
        adminPages.add(menuPanel(), "Menu");
        adminPages.add(toppingPanel(), "Topping");
        adminPages.add(inventoryPanel(), "Inventory");
        adminPages.add(usersPanel(), "Users");
        setContentPane(AppShell.wrap(this, context, "Admin",
                "Manage menu, orders, inventory, users, and reports.",
                adminPages, this::selectAdminPage, "Overview", "Orders", "Menu", "Topping", "Inventory", "Users"));
    }

    private void selectAdminPage(String nav) {
        adminLayout.show(adminPages, nav);
        if ("Overview".equals(nav)) {
            refreshOverview();
        }
        if ("Orders".equals(nav)) refreshOrderModels();
        if ("Inventory".equals(nav)) refreshInventoryModel();
        if ("Users".equals(nav)) refreshUserModel();
    }

    private JPanel overviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(24, 24));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));
        JButton refresh = AppTheme.ghostButton("Refresh overview");
        refresh.addActionListener(e -> {
            refreshOverview();
            panel.repaint();
        });

        JPanel cards = new JPanel(new GridLayout(1, 3, 20, 20));
        cards.setOpaque(false);
        cards.add(metricCard("Total orders", totalOrdersMetric));
        cards.add(metricCard("Paid", paidOrdersMetric));
        cards.add(metricCard("Revenue", revenueMetric));

        menuOverviewChart.setBorder(BorderFactory.createTitledBorder("Menu"));
        toppingOverviewChart.setBorder(BorderFactory.createTitledBorder("Topping"));
        refreshOverview();
        JPanel charts = AppTheme.roundedPanel(new GridLayout(1, 2, 16, 0), AppTheme.PANEL, null, 16, new Insets(16, 16, 16, 16));
        charts.add(menuOverviewChart);
        charts.add(toppingOverviewChart);
        panel.add(cards, BorderLayout.NORTH);
        panel.add(charts, BorderLayout.CENTER);
        panel.add(refresh, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshOverview() {
        totalOrdersMetric.setText(String.valueOf(countVisibleOrders()));
        paidOrdersMetric.setText(String.valueOf(countPaidOrders()));
        revenueMetric.setText(String.format("%,.0f d", context.reportService.getRevenue()));
        menuOverviewChart.setData(context.reportService.getMenuSales());
        toppingOverviewChart.setData(context.reportService.getToppingSales());
        menuOverviewChart.revalidate();
        toppingOverviewChart.revalidate();
        menuOverviewChart.repaint();
        toppingOverviewChart.repaint();
    }

    private JPanel metricCard(String title, JLabel valueLabel) {
        JPanel card = AppTheme.roundedPanel(new GridLayout(2, 1, 0, 8), AppTheme.PANEL, null, 16, new Insets(20, 20, 20, 20));
        valueLabel.setForeground(AppTheme.PRIMARY);
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 24f));
        JLabel titleLabel = AppTheme.muted(title);
        card.add(valueLabel);
        card.add(titleLabel);
        return card;
    }

    private JPanel sectionHeader(String title, String subtitle) {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 2));
        panel.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setForeground(AppTheme.TEXT);
        t.setFont(t.getFont().deriveFont(Font.BOLD, 20f));
        JLabel s = AppTheme.muted(subtitle);
        panel.add(t);
        panel.add(s);
        return panel;
    }

    private JPanel wrapWithTitle(String title, JComponent component) {
        JPanel panel = AppTheme.card(new BorderLayout(16, 16));
        JLabel label = AppTheme.section(title);
        panel.add(label, BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private JPanel menuPanel() {
        JPanel panel = new JPanel(new BorderLayout(24, 24));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));
        menuList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        menuList.setCellRenderer(new MenuRenderer());
        menuList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillBeverageForm(menuList.getSelectedValue());
            }
        });
        panel.add(wrapWithTitle("Menu catalog", new JScrollPane(menuList)), BorderLayout.CENTER);
        JPanel side = new JPanel(new BorderLayout(0, 16));
        side.setOpaque(false);
        side.setPreferredSize(new Dimension(440, 0));
        side.add(beverageForm(), BorderLayout.NORTH);
        side.add(recipeForm(), BorderLayout.CENTER);
        panel.add(side, BorderLayout.EAST);
        refreshMenuModel();
        refreshInventoryModel();
        return panel;
    }

    private JPanel toppingPanel() {
        JPanel panel = new JPanel(new BorderLayout(24, 24));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));
        toppingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        toppingList.setCellRenderer(new ToppingRenderer());
        toppingList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillToppingForm(toppingList.getSelectedValue());
            }
        });
        panel.add(wrapWithTitle("Topping catalog", new JScrollPane(toppingList)), BorderLayout.CENTER);
        panel.add(toppingForm(), BorderLayout.EAST);
        refreshToppingModel();
        return panel;
    }

    private JPanel ordersPanel() {
        JPanel panel = new JPanel(new BorderLayout(24, 24));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));
        activeOrderDetailArea.setEditable(false);
        activeOrderDetailArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        activeOrderDetailArea.setBorder(new EmptyBorder(12, 12, 12, 12));
        activeOrderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        activeOrderList.setCellRenderer(new AdminOrderRenderer());
        activeOrderList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                activeOrderDetailArea.setText(orderDetails(activeOrderList.getSelectedValue()));
            }
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        JButton refresh = AppTheme.ghostButton("Refresh");
        JButton cancel = AppTheme.button("Cancel order", AppTheme.DANGER);
        actions.add(refresh);
        actions.add(cancel);

        refresh.addActionListener(e -> refreshOrderModels());
        cancel.addActionListener(e -> selectedAdminOrder(order -> context.orderService.cancel(order)));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(activeOrderList), new JScrollPane(activeOrderDetailArea));
        split.setDividerLocation(320);
        split.setBorder(null);
        panel.add(sectionHeader("Active Orders", "State Pattern controls which transitions are allowed."), BorderLayout.NORTH);
        panel.add(split, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        refreshOrderModels();
        return panel;
    }

    private JPanel historyPanel() {
        JPanel panel = new JPanel(new BorderLayout(24, 24));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));
        historyOrderDetailArea.setEditable(false);
        historyOrderDetailArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        historyOrderDetailArea.setBorder(new EmptyBorder(12, 12, 12, 12));
        historyOrderList.setCellRenderer(new AdminOrderRenderer());
        historyOrderList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                historyOrderDetailArea.setText(orderDetails(historyOrderList.getSelectedValue()));
            }
        });
        JButton refresh = AppTheme.ghostButton("Refresh history");
        refresh.addActionListener(e -> refreshOrderModels());
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(historyOrderList), new JScrollPane(historyOrderDetailArea));
        split.setDividerLocation(320);
        split.setBorder(null);
        panel.add(split, BorderLayout.CENTER);
        panel.add(refresh, BorderLayout.SOUTH);
        refreshOrderModels();
        return panel;
    }

    private JPanel reportPanel() {
        JPanel panel = new JPanel(new BorderLayout(24, 24));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));
        JButton refresh = AppTheme.ghostButton("Refresh report");
        reportArea.setEditable(false);
        reportArea.setBorder(new EmptyBorder(12, 12, 12, 12));
        refresh.addActionListener(e -> refreshReport());
        JPanel top = AppTheme.roundedPanel(new BorderLayout(), AppTheme.PANEL, null, 16, new Insets(16, 20, 16, 20));
        revenueCard.setFont(revenueCard.getFont().deriveFont(Font.BOLD, 18f));
        revenueCard.setForeground(AppTheme.PRIMARY);
        top.add(revenueCard, BorderLayout.WEST);
        top.add(refresh, BorderLayout.EAST);
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, reportStatsPanel(), new JScrollPane(reportArea));
        split.setDividerLocation(280);
        split.setBorder(null);
        panel.add(top, BorderLayout.NORTH);
        panel.add(split, BorderLayout.CENTER);
        refreshReport();
        return panel;
    }

    private JPanel reportStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 16, 0));
        panel.setOpaque(false);
        panel.add(reportTableCard("Menu sales", menuReportModel));
        panel.add(reportTableCard("Topping sales", toppingReportModel));
        return panel;
    }

    private JPanel reportTableCard(String title, DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setFillsViewportHeight(true);
        table.setForeground(AppTheme.TEXT);
        table.setBackground(AppTheme.SURFACE);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD, 12f));
        table.getTableHeader().setForeground(AppTheme.MUTED);
        table.getTableHeader().setBackground(AppTheme.PANEL);
        table.getColumnModel().getColumn(1).setMaxWidth(80);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        scroll.getViewport().setBackground(AppTheme.SURFACE);
        return wrapWithTitle(title, scroll);
    }

    private JPanel operationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(24, 24));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));

        JButton refresh = AppTheme.ghostButton("Refresh operations");
        JButton backup = AppTheme.button("Backup SQLite", AppTheme.SUCCESS);
        backup.setEnabled(context.operationsService.canBackup());
        refresh.addActionListener(e -> refreshOperationsPanel());
        backup.addActionListener(e -> createBackup());

        JPanel top = AppTheme.roundedPanel(new BorderLayout(), AppTheme.PANEL, null, 16, new Insets(16, 20, 16, 20));
        top.add(sectionHeader("Operations", "Audit trail, stock ledger, and SQLite backup."), BorderLayout.WEST);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(refresh);
        actions.add(backup);
        top.add(actions, BorderLayout.EAST);

        statusHistoryArea.setEditable(false);
        statusHistoryArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        statusHistoryArea.setBorder(new EmptyBorder(12, 12, 12, 12));
        inventoryTransactionArea.setEditable(false);
        inventoryTransactionArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        inventoryTransactionArea.setBorder(new EmptyBorder(12, 12, 12, 12));
        operationsSummaryArea.setEditable(false);
        operationsSummaryArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        operationsSummaryArea.setBorder(new EmptyBorder(12, 12, 12, 12));

        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                wrapWithTitle("Order status audit", new JScrollPane(statusHistoryArea)),
                wrapWithTitle("Inventory ledger", new JScrollPane(inventoryTransactionArea))
        );
        split.setDividerLocation(260);
        split.setBorder(null);

        panel.add(top, BorderLayout.NORTH);
        panel.add(split, BorderLayout.CENTER);
        panel.add(wrapWithTitle("System summary", new JScrollPane(operationsSummaryArea)), BorderLayout.SOUTH);
        refreshOperationsPanel();
        return panel;
    }

    private JPanel inventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(24, 24));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));
        JList<InventoryItem> inventoryList = new JList<>(inventoryModel);
        inventoryList.setCellRenderer(new InventoryRenderer());
        inventoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton refresh = AppTheme.ghostButton("Refresh inventory");
        JButton restock = AppTheme.button("Restock selected", AppTheme.SUCCESS);
        actions.add(refresh);
        actions.add(restock);
        
        refresh.addActionListener(e -> refreshInventoryModel());
        restock.addActionListener(e -> {
            InventoryItem selected = inventoryList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select an item to restock.");
                return;
            }
            String input = JOptionPane.showInputDialog(this, "Enter restock amount for " + selected.getName() + " (" + selected.getUnit() + "):", "Restock", JOptionPane.QUESTION_MESSAGE);
            if (input != null && !input.trim().isEmpty()) {
                try {
                    double amount = Double.parseDouble(input.trim());
                    context.inventoryService.restockItem(selected.getId(), amount);
                    refreshInventoryModel();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid amount.", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (RuntimeException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Restock error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        panel.add(sectionHeader("Inventory", "InventoryService deducts stock when orders enter kitchen flow."), BorderLayout.NORTH);
        panel.add(new JScrollPane(inventoryList), BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        refreshInventoryModel();
        return panel;
    }

    private JPanel usersPanel() {
        JPanel panel = new JPanel(new BorderLayout(24, 24));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value.getUsername() + " [" + value.getRole() + "] - " + (value.isActive() ? "ACTIVE" : "LOCKED"));
            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
            label.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            label.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            return label;
        });
        panel.add(new JScrollPane(userList), BorderLayout.CENTER);
        panel.add(userForm(), BorderLayout.EAST);
        refreshUserModel();
        return panel;
    }

    private void refreshReport() {
        StringBuilder builder = new StringBuilder();
        revenueCard.setText("Revenue: " + String.format("%,.0f", context.reportService.getRevenue()) + "d");
        refreshReportTable(menuReportModel, context.reportService.getMenuSales());
        refreshReportTable(toppingReportModel, context.reportService.getToppingSales());
        builder.append("Revenue: ").append(String.format("%,.0f", context.reportService.getRevenue())).append("d\n\n");
        builder.append("Top selling items:\n");
        for (Map.Entry<String, Long> entry : context.reportService.getTopSellingItems().entrySet()) {
            builder.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        reportArea.setText(builder.toString());
    }

    private void refreshReportTable(DefaultTableModel model, Map<String, Long> data) {
        model.setRowCount(0);
        for (Map.Entry<String, Long> entry : data.entrySet()) {
            model.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }
    }

    private void refreshOperationsPanel() {
        StringBuilder historyBuilder = new StringBuilder();
        for (OrderStatusHistoryRecord record : context.operationsService.getOrderStatusHistory()) {
            historyBuilder.append(record.getChangedAt().format(DateTimeFormatter.ofPattern("dd/MM HH:mm:ss")))
                    .append(" | Order #").append(record.getOrderId())
                    .append(" | ").append(record.getStatus());
            if (record.getNote() != null && !record.getNote().isBlank()) {
                historyBuilder.append(" | ").append(record.getNote());
            }
            historyBuilder.append("\n");
        }
        if (historyBuilder.length() == 0) {
            historyBuilder.append("No order status activity yet.");
        }
        statusHistoryArea.setText(historyBuilder.toString());
        statusHistoryArea.setCaretPosition(0);

        StringBuilder inventoryBuilder = new StringBuilder();
        for (InventoryTransactionRecord record : context.operationsService.getInventoryTransactions()) {
            inventoryBuilder.append(record.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM HH:mm:ss")))
                    .append(" | ").append(record.getInventoryItemName())
                    .append(" | ").append(String.format("%+.0f", record.getChangeAmount()))
                    .append(" | balance ").append(String.format("%,.0f", record.getBalanceAfter()))
                    .append(" | ").append(record.getReason());
            if (record.getOrderId() != null) {
                inventoryBuilder.append(" | order #").append(record.getOrderId());
            }
            inventoryBuilder.append("\n");
        }
        if (inventoryBuilder.length() == 0) {
            inventoryBuilder.append("No inventory transactions yet.");
        }
        inventoryTransactionArea.setText(inventoryBuilder.toString());
        inventoryTransactionArea.setCaretPosition(0);

        StringBuilder summaryBuilder = new StringBuilder();
        summaryBuilder.append("Storage: ").append(context.operationsService.getStorageLabel()).append("\n");
        summaryBuilder.append("Order status records: ").append(context.operationsService.getOrderStatusHistory().size()).append("\n");
        summaryBuilder.append("Inventory transactions: ").append(context.operationsService.getInventoryTransactions().size()).append("\n");
        summaryBuilder.append("Backup available: ").append(context.operationsService.canBackup() ? "YES" : "NO");
        operationsSummaryArea.setText(summaryBuilder.toString());
        operationsSummaryArea.setCaretPosition(0);
    }

    private void createBackup() {
        try {
            Path backupPath = context.operationsService.backupDatabase();
            refreshOperationsPanel();
            JOptionPane.showMessageDialog(this, "Backup created at:\n" + backupPath.toAbsolutePath());
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Backup error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private JPanel beverageForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setPreferredSize(new Dimension(440, 260));
        form.setMinimumSize(new Dimension(380, 240));
        form.setBorder(BorderFactory.createTitledBorder("Menu item"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        addRow(form, gbc, 0, "Name", beverageNameField);
        addRow(form, gbc, 1, "Price", beveragePriceField);
        addRow(form, gbc, 2, "Category", categoryBox);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; form.add(beverageActiveBox, gbc);

        JButton add = compactAdminButton("Add", AppTheme.SUCCESS);
        JButton update = compactAdminButton("Update", AppTheme.WARNING);
        JButton disable = compactAdminButton("Disable", AppTheme.DANGER);
        JButton clear = compactAdminGhostButton("Clear");
        JPanel buttons = new JPanel(new GridLayout(2, 2, 10, 8));
        buttons.setOpaque(false);
        buttons.add(add); buttons.add(update); buttons.add(disable); buttons.add(clear);
        gbc.gridy = 4; form.add(buttons, gbc);

        add.addActionListener(e -> runAdminAction(() -> {
            context.menuService.addBeverage(beverageNameField.getText(), parsePrice(beveragePriceField), (String) categoryBox.getSelectedItem());
            clearBeverageForm();
            refreshMenuModel();
        }));
        update.addActionListener(e -> runAdminAction(() -> {
            context.menuService.updateBeverage(menuList.getSelectedValue(), beverageNameField.getText(), parsePrice(beveragePriceField),
                    (String) categoryBox.getSelectedItem(), beverageActiveBox.isSelected());
            refreshMenuModel();
        }));
        disable.addActionListener(e -> runAdminAction(() -> {
            context.menuService.disableBeverage(menuList.getSelectedValue());
            refreshMenuModel();
        }));
        clear.addActionListener(e -> clearBeverageForm());
        return form;
    }

    private JPanel recipeForm() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(440, 360));
        panel.setMinimumSize(new Dimension(380, 260));
        panel.setBorder(BorderFactory.createTitledBorder("Recipe"));

        recipeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recipeList.setVisibleRowCount(7);
        recipeList.setCellRenderer(new RecipeRenderer());
        recipeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillRecipeForm(recipeList.getSelectedValue());
            }
        });
        panel.add(new JScrollPane(recipeList), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        recipeInventoryBox.setRenderer(new InventoryComboRenderer());
        loadRecipeInventoryOptions();
        addRow(form, gbc, 0, "Ingredient", recipeInventoryBox);
        addRow(form, gbc, 1, "Quantity", recipeQuantityField);

        JButton add = compactAdminButton("Save line", AppTheme.SUCCESS);
        JButton remove = compactAdminButton("Remove line", AppTheme.DANGER);
        JButton clear = compactAdminGhostButton("Clear");
        JPanel buttons = new JPanel(new GridLayout(1, 3, 10, 0));
        buttons.setOpaque(false);
        buttons.add(add);
        buttons.add(remove);
        buttons.add(clear);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        form.add(buttons, gbc);

        add.addActionListener(e -> runAdminAction(() -> {
            context.menuService.saveRecipeItem(
                    menuList.getSelectedValue(),
                    (InventoryItem) recipeInventoryBox.getSelectedItem(),
                    parsePositiveNumber(recipeQuantityField, "Recipe quantity")
            );
            refreshRecipeModel(menuList.getSelectedValue());
        }));
        remove.addActionListener(e -> runAdminAction(() -> {
            RecipeRow row = recipeList.getSelectedValue();
            context.menuService.deleteRecipeItem(menuList.getSelectedValue(), row == null ? null : row.inventoryItem());
            refreshRecipeModel(menuList.getSelectedValue());
        }));
        clear.addActionListener(e -> clearRecipeForm());

        panel.add(form, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel userForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setPreferredSize(new Dimension(300, 0));
        form.setBorder(BorderFactory.createTitledBorder("User"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        addRow(form, gbc, 0, "Username", usernameField);
        addRow(form, gbc, 1, "Password", passwordField);
        addRow(form, gbc, 2, "Role", roleBox);

        JButton add = compactAdminButton("Add user", AppTheme.SUCCESS);
        JButton lock = compactAdminButton("Lock", AppTheme.DANGER);
        JButton unlock = compactAdminButton("Unlock", AppTheme.WARNING);
        JButton clear = compactAdminGhostButton("Clear");
        JPanel buttons = new JPanel(new GridLayout(2, 2, 10, 8));
        buttons.setOpaque(false);
        buttons.add(add); buttons.add(lock); buttons.add(unlock); buttons.add(clear);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        form.add(buttons, gbc);

        add.addActionListener(e -> runAdminAction(() -> {
            context.userService.addUser(usernameField.getText(), new String(passwordField.getPassword()), (String) roleBox.getSelectedItem());
            clearUserForm();
            refreshUserModel();
        }));
        lock.addActionListener(e -> runAdminAction(() -> {
            context.userService.setActive(userList.getSelectedValue(), false);
            refreshUserModel();
        }));
        unlock.addActionListener(e -> runAdminAction(() -> {
            context.userService.setActive(userList.getSelectedValue(), true);
            refreshUserModel();
        }));
        clear.addActionListener(e -> clearUserForm());
        return form;
    }

    private JPanel toppingForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setPreferredSize(new Dimension(280, 0));
        form.setBorder(BorderFactory.createTitledBorder("Topping"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        addRow(form, gbc, 0, "Name", toppingNameField);
        addRow(form, gbc, 1, "Price", toppingPriceField);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; form.add(toppingActiveBox, gbc);

        JButton add = compactAdminButton("Add", AppTheme.SUCCESS);
        JButton update = compactAdminButton("Update", AppTheme.WARNING);
        JButton disable = compactAdminButton("Disable", AppTheme.DANGER);
        JButton clear = compactAdminGhostButton("Clear");
        JPanel buttons = new JPanel(new GridLayout(2, 2, 10, 8));
        buttons.setOpaque(false);
        buttons.add(add); buttons.add(update); buttons.add(disable); buttons.add(clear);
        gbc.gridy = 3; form.add(buttons, gbc);

        add.addActionListener(e -> runAdminAction(() -> {
            context.menuService.addTopping(toppingNameField.getText(), parsePrice(toppingPriceField));
            clearToppingForm();
            refreshToppingModel();
        }));
        update.addActionListener(e -> runAdminAction(() -> {
            context.menuService.updateTopping(toppingList.getSelectedValue(), toppingNameField.getText(),
                    parsePrice(toppingPriceField), toppingActiveBox.isSelected());
            refreshToppingModel();
        }));
        disable.addActionListener(e -> runAdminAction(() -> {
            context.menuService.disableTopping(toppingList.getSelectedValue());
            refreshToppingModel();
        }));
        clear.addActionListener(e -> clearToppingForm());
        return form;
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent component) {
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(component, gbc);
    }

    private JButton compactAdminButton(String text, Color color) {
        JButton button = AppTheme.button(text, color);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 12f));
        button.setMargin(new Insets(6, 10, 6, 10));
        button.setPreferredSize(new Dimension(0, 38));
        return button;
    }

    private JButton compactAdminGhostButton(String text) {
        JButton button = AppTheme.ghostButton(text);
        button.setFont(button.getFont().deriveFont(Font.PLAIN, 12f));
        button.setMargin(new Insets(6, 10, 6, 10));
        button.setPreferredSize(new Dimension(0, 38));
        return button;
    }

    private void refreshMenuModel() {
        menuModel.clear();
        context.menuService.getAllMenu().forEach(menuModel::addElement);
        refreshRecipeModel(menuList.getSelectedValue());
    }

    private void refreshToppingModel() {
        toppingModel.clear();
        context.menuService.getAllToppings().forEach(toppingModel::addElement);
    }

    private void refreshInventoryModel() {
        inventoryModel.clear();
        context.inventoryService.getInventory().forEach(inventoryModel::addElement);
        loadRecipeInventoryOptions();
        refreshRecipeModel(menuList.getSelectedValue());
    }

    private void refreshOrderModels() {
        activeOrderModel.clear();
        historyOrderModel.clear();
        context.repository.getOrders().forEach(order -> {
            if (isJunkOrder(order)) {
                return;
            }
            historyOrderModel.addElement(order);
            if (!"PAID".equals(order.getStatus()) && !"CANCELLED".equals(order.getStatus())) {
                activeOrderModel.addElement(order);
            }
        });
        activeOrderDetailArea.setText("");
        historyOrderDetailArea.setText("");
    }

    private boolean isJunkOrder(Order order) {
        return order == null || order.getItems().isEmpty() || order.getTotalAmount() <= 0;
    }

    private void refreshUserModel() {
        userModel.clear();
        context.userService.getUsers().forEach(userModel::addElement);
    }

    private void refreshRecipeModel(MenuItemRecord beverage) {
        recipeModel.clear();
        if (beverage == null) {
            clearRecipeForm();
            return;
        }
        for (RecipeItem item : context.menuService.getRecipeItems(beverage)) {
            InventoryItem inventoryItem = context.inventoryService.getInventory().stream()
                    .filter(candidate -> candidate.getId() == item.getInventoryItemId())
                    .findFirst()
                    .orElse(null);
            if (inventoryItem != null) {
                recipeModel.addElement(new RecipeRow(item, inventoryItem));
            }
        }
        clearRecipeForm();
    }

    private void loadRecipeInventoryOptions() {
        InventoryItem selected = (InventoryItem) recipeInventoryBox.getSelectedItem();
        DefaultComboBoxModel<InventoryItem> model = new DefaultComboBoxModel<>();
        for (InventoryItem item : context.inventoryService.getInventory()) {
            model.addElement(item);
        }
        recipeInventoryBox.setModel(model);
        if (selected != null) {
            for (int i = 0; i < model.getSize(); i++) {
                if (model.getElementAt(i).getId() == selected.getId()) {
                    recipeInventoryBox.setSelectedIndex(i);
                    return;
                }
            }
        }
        if (model.getSize() > 0) {
            recipeInventoryBox.setSelectedIndex(0);
        }
    }

    private void fillBeverageForm(MenuItemRecord item) {
        if (item == null) {
            clearRecipeForm();
            return;
        }
        beverageNameField.setText(item.getName());
        beveragePriceField.setText(String.format("%.0f", item.getBasePrice()));
        categoryBox.setSelectedItem(item.getCategory());
        beverageActiveBox.setSelected(item.isActive());
        refreshRecipeModel(item);
    }

    private void fillToppingForm(Topping topping) {
        if (topping == null) return;
        toppingNameField.setText(topping.getName());
        toppingPriceField.setText(String.format("%.0f", topping.getExtraPrice()));
        toppingActiveBox.setSelected(topping.isActive());
    }

    private void fillRecipeForm(RecipeRow row) {
        if (row == null) {
            clearRecipeForm();
            return;
        }
        selectRecipeInventory(row.inventoryItem().getId());
        recipeQuantityField.setText(formatQuantity(row.recipeItem().getQuantityRequired()));
    }

    private void clearBeverageForm() {
        menuList.clearSelection();
        beverageNameField.setText("");
        beveragePriceField.setText("");
        categoryBox.setSelectedItem("COFFEE");
        beverageActiveBox.setSelected(true);
        recipeModel.clear();
        clearRecipeForm();
    }

    private void clearToppingForm() {
        toppingList.clearSelection();
        toppingNameField.setText("");
        toppingPriceField.setText("");
        toppingActiveBox.setSelected(true);
    }

    private void clearRecipeForm() {
        recipeList.clearSelection();
        recipeQuantityField.setText("");
        if (recipeInventoryBox.getItemCount() > 0 && recipeInventoryBox.getSelectedIndex() < 0) {
            recipeInventoryBox.setSelectedIndex(0);
        }
    }

    private void clearUserForm() {
        userList.clearSelection();
        usernameField.setText("");
        passwordField.setText("");
        roleBox.setSelectedItem("CASHIER");
    }

    private long countOrders(String status) {
        return context.repository.getOrders().stream()
                .filter(order -> !isJunkOrder(order))
                .filter(order -> status.equals(order.getStatus()))
                .count();
    }

    private long countVisibleOrders() {
        return context.repository.getOrders().stream()
                .filter(order -> !isJunkOrder(order))
                .count();
    }

    private long countPaidOrders() {
        return context.repository.getOrders().stream()
                .filter(order -> !isJunkOrder(order))
                .filter(order -> order.getPayment() != null && "SUCCESS".equals(order.getPayment().getStatus()))
                .filter(order -> !"CANCELLED".equals(order.getStatus()))
                .count();
    }

    private void selectedAdminOrder(java.util.function.Consumer<Order> action) {
        Order order = activeOrderList.getSelectedValue();
        if (order == null) return;
        try {
            action.accept(order);
            refreshOrderModels();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Order error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private String orderDetails(Order order) {
        if (order == null) return "";
        StringBuilder builder = new StringBuilder();
        builder.append("Order #").append(order.getId()).append("\n");
        builder.append("Created: ").append(order.getCreatedAt()).append("\n");
        builder.append("Status: ").append(order.getStatus()).append("\n");
        builder.append("Discount: ").append(order.getDiscountType()).append(" / ")
                .append(String.format("%,.0f", order.getDiscountAmount())).append("d\n\n");
        for (OrderItem item : order.getItems()) {
            builder.append(item).append("\n");
        }
        builder.append("\nSubtotal: ").append(String.format("%,.0f", order.getSubtotal())).append("d");
        builder.append("\nTotal: ").append(String.format("%,.0f", order.getTotalAmount())).append("d");
        if (order.getPayment() != null) {
            builder.append("\nPayment: ").append(order.getPayment().getMethod())
                    .append(" / ").append(order.getPayment().getTransactionCode());
        }
        return builder.toString();
    }

    private double parsePrice(JTextField field) {
        try {
            return Double.parseDouble(field.getText().trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Price must be a valid number.");
        }
    }

    private double parsePositiveNumber(JTextField field, String fieldName) {
        try {
            double value = Double.parseDouble(field.getText().trim());
            if (value <= 0) {
                throw new IllegalArgumentException(fieldName + " must be greater than 0.");
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " must be a valid number.");
        }
    }

    private void selectRecipeInventory(int inventoryItemId) {
        for (int i = 0; i < recipeInventoryBox.getItemCount(); i++) {
            InventoryItem item = recipeInventoryBox.getItemAt(i);
            if (item.getId() == inventoryItemId) {
                recipeInventoryBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private String formatQuantity(double value) {
        if (Math.abs(value - Math.rint(value)) < 0.001) {
            return String.format("%.0f", value);
        }
        return String.format("%.2f", value);
    }

    private void runAdminAction(Runnable action) {
        try {
            action.run();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private static final class MenuRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
            MenuItemRecord item = (MenuItemRecord) value;
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
            label.setText("<html><b>" + item.getName() + "</b> <span style='color:#735f55'>[" + item.getCategory()
                    + "]</span><br>" + AppTheme.money(item.getBasePrice()) + " - "
                    + (item.isActive() ? "ACTIVE" : "DISABLED") + "</html>");
            label.setBorder(new EmptyBorder(10, 12, 10, 12));
            label.setBackground(selected ? new Color(255, 245, 235) : AppTheme.PANEL);
            label.setForeground(AppTheme.TEXT);
            return label;
        }
    }

    private static final class ToppingRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
            Topping topping = (Topping) value;
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
            label.setText("<html><b>" + topping.getName() + "</b><br>" + AppTheme.money(topping.getExtraPrice())
                    + " - " + (topping.isActive() ? "ACTIVE" : "DISABLED") + "</html>");
            label.setBorder(new EmptyBorder(10, 12, 10, 12));
            label.setBackground(selected ? new Color(255, 245, 235) : AppTheme.PANEL);
            label.setForeground(AppTheme.TEXT);
            return label;
        }
    }

    private static final class RecipeRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
            RecipeRow row = (RecipeRow) value;
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
            label.setText("<html><b>" + row.inventoryItem().getName() + "</b><br>"
                    + String.format("%,.2f", row.recipeItem().getQuantityRequired()) + " " + row.inventoryItem().getUnit() + "</html>");
            label.setBorder(new EmptyBorder(8, 10, 8, 10));
            label.setBackground(selected ? new Color(255, 245, 235) : AppTheme.PANEL);
            label.setForeground(AppTheme.TEXT);
            return label;
        }
    }

    private static final class InventoryComboRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
            InventoryItem item = (InventoryItem) value;
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
            if (item != null) {
                label.setText(item.getName() + " (" + item.getUnit() + ")");
            }
            return label;
        }
    }

    private static final class InventoryRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
            InventoryItem item = (InventoryItem) value;
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
            boolean low = item.isLowStock();
            label.setText("<html><b>" + item.getName() + "</b><br>" + String.format("%,.0f", item.getQuantity())
                    + " " + item.getUnit() + " / reorder at " + String.format("%,.0f", item.getReorderLevel()) + "</html>");
            label.setBorder(new EmptyBorder(10, 12, 10, 12));
            label.setBackground(selected ? new Color(255, 241, 225) : (low ? new Color(255, 238, 228) : AppTheme.PANEL));
            label.setForeground(low ? AppTheme.DANGER : AppTheme.TEXT);
            return label;
        }
    }

    private static final class AdminOrderRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
            Order order = (Order) value;
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
            label.setText("<html><b>Order #" + order.getId() + "</b> - " + order.getStatus()
                    + "<br>" + order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))
                    + " - " + order.getItems().size() + " line(s) - " + AppTheme.money(order.getTotalAmount()) + "</html>");
            label.setBorder(new EmptyBorder(10, 12, 10, 12));
            label.setBackground(selected ? new Color(255, 245, 235) : AppTheme.PANEL);
            label.setForeground(switch (order.getStatus()) {
                case "PAID" -> AppTheme.SUCCESS;
                case "CANCELLED" -> AppTheme.DANGER;
                case "PREPARING" -> AppTheme.WARNING;
                default -> AppTheme.TEXT;
            });
            return label;
        }
    }

    private record RecipeRow(RecipeItem recipeItem, InventoryItem inventoryItem) {
    }
}
