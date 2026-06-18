package com.coffeeshop.presentation;

import com.coffeeshop.AppContext;
import com.coffeeshop.domain.model.InventoryItem;
import com.coffeeshop.domain.model.Order;
import com.coffeeshop.domain.model.OrderItem;
import com.coffeeshop.domain.model.Topping;
import com.coffeeshop.domain.model.User;
import com.coffeeshop.infrastructure.MenuItemRecord;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class AdminView extends JFrame {
    private final AppContext context;
    private final JTextArea reportArea = new JTextArea();
    private final BarChartPanel topItemsChart = new BarChartPanel();
    private final JLabel revenueCard = new JLabel("Revenue: 0d");
    private final DefaultListModel<MenuItemRecord> menuModel = new DefaultListModel<>();
    private final DefaultListModel<Topping> toppingModel = new DefaultListModel<>();
    private final DefaultListModel<InventoryItem> inventoryModel = new DefaultListModel<>();
    private final DefaultListModel<Order> activeOrderModel = new DefaultListModel<>();
    private final DefaultListModel<Order> historyOrderModel = new DefaultListModel<>();
    private final DefaultListModel<User> userModel = new DefaultListModel<>();
    private final JList<MenuItemRecord> menuList = new JList<>(menuModel);
    private final JList<Topping> toppingList = new JList<>(toppingModel);
    private final JList<Order> activeOrderList = new JList<>(activeOrderModel);
    private final JList<Order> historyOrderList = new JList<>(historyOrderModel);
    private final JList<User> userList = new JList<>(userModel);
    private final JTextArea activeOrderDetailArea = new JTextArea();
    private final JTextArea historyOrderDetailArea = new JTextArea();
    private final JTextField beverageNameField = new JTextField();
    private final JTextField beveragePriceField = new JTextField();
    private final JComboBox<String> categoryBox = new JComboBox<>(new String[]{"COFFEE", "TEA", "MATCHA", "SMOOTHIE"});
    private final JCheckBox beverageActiveBox = new JCheckBox("Active", true);
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

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(tabs.getFont().deriveFont(Font.BOLD, 13f));
        tabs.addTab("Overview", overviewPanel());
        tabs.addTab("Orders", ordersPanel());
        tabs.addTab("History", historyPanel());
        tabs.addTab("Menu", menuPanel());
        tabs.addTab("Topping", toppingPanel());
        tabs.addTab("Inventory", inventoryPanel());
        tabs.addTab("Users", usersPanel());
        tabs.addTab("Revenue report", reportPanel());
        setContentPane(AppShell.wrap(this, context, "Admin",
                "Manage menu, orders, inventory, users, and reports.",
                tabs, nav -> selectAdminTab(tabs, nav), "Overview", "Orders", "History", "Menu", "Topping", "Inventory", "Users", "Reports"));
    }

    private void selectAdminTab(JTabbedPane tabs, String nav) {
        String target = "Reports".equals(nav) ? "Revenue report" : nav;
        for (int i = 0; i < tabs.getTabCount(); i++) {
            if (tabs.getTitleAt(i).equals(target)) {
                tabs.setSelectedIndex(i);
                if ("Overview".equals(target)) {
                    topItemsChart.setData(context.reportService.getTopSellingItems());
                }
                if ("Orders".equals(target) || "History".equals(target)) refreshOrderModels();
                if ("Inventory".equals(target)) refreshInventoryModel();
                if ("Users".equals(target)) refreshUserModel();
                if ("Revenue report".equals(target)) refreshReport();
                return;
            }
        }
    }

    private JPanel overviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(24, 24));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));
        JButton refresh = AppTheme.ghostButton("Refresh overview");
        refresh.addActionListener(e -> {
            topItemsChart.setData(context.reportService.getTopSellingItems());
            panel.repaint();
        });

        JPanel cards = new JPanel(new GridLayout(1, 4, 20, 20));
        cards.setOpaque(false);
        cards.add(metricCard("Total orders", String.valueOf(context.repository.getOrders().size())));
        cards.add(metricCard("Pending", String.valueOf(countOrders("PENDING"))));
        cards.add(metricCard("Paid", String.valueOf(countOrders("PAID"))));
        cards.add(metricCard("Revenue", String.format("%,.0f d", context.reportService.getRevenue())));

        topItemsChart.setData(context.reportService.getTopSellingItems());
        topItemsChart.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        panel.add(cards, BorderLayout.NORTH);
        panel.add(AppTheme.roundedPanel(new BorderLayout(), AppTheme.PANEL, null, 16, new Insets(16, 16, 16, 16)), BorderLayout.CENTER);
        ((JPanel) panel.getComponent(1)).add(topItemsChart, BorderLayout.CENTER);
        panel.add(refresh, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel metricCard(String title, String value) {
        JPanel card = AppTheme.roundedPanel(new GridLayout(2, 1, 0, 8), AppTheme.PANEL, null, 16, new Insets(20, 20, 20, 20));
        JLabel valueLabel = new JLabel(value);
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
        panel.add(beverageForm(), BorderLayout.EAST);
        refreshMenuModel();
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
        JButton sendKitchen = AppTheme.button("Send to kitchen", AppTheme.WARNING);
        JButton ready = AppTheme.button("Mark ready", AppTheme.SUCCESS);
        JButton cancel = AppTheme.button("Cancel order", AppTheme.DANGER);
        actions.add(refresh);
        actions.add(sendKitchen);
        actions.add(ready);
        actions.add(cancel);

        refresh.addActionListener(e -> refreshOrderModels());
        sendKitchen.addActionListener(e -> selectedAdminOrder(order -> context.orderService.sendToKitchen(order)));
        ready.addActionListener(e -> selectedAdminOrder(order -> context.orderService.markReady(order)));
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
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topItemsChart, new JScrollPane(reportArea));
        split.setDividerLocation(260);
        split.setBorder(null);
        panel.add(top, BorderLayout.NORTH);
        panel.add(split, BorderLayout.CENTER);
        refreshReport();
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
        topItemsChart.setData(context.reportService.getTopSellingItems());
        builder.append("Revenue: ").append(String.format("%,.0f", context.reportService.getRevenue())).append("d\n\n");
        builder.append("Top selling items:\n");
        for (Map.Entry<String, Long> entry : context.reportService.getTopSellingItems().entrySet()) {
            builder.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        reportArea.setText(builder.toString());
    }

    private JPanel beverageForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setPreferredSize(new Dimension(280, 0));
        form.setBorder(BorderFactory.createTitledBorder("Menu item"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        addRow(form, gbc, 0, "Name", beverageNameField);
        addRow(form, gbc, 1, "Price", beveragePriceField);
        addRow(form, gbc, 2, "Category", categoryBox);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; form.add(beverageActiveBox, gbc);

        JButton add = AppTheme.button("Add", AppTheme.SUCCESS);
        JButton update = AppTheme.button("Update", AppTheme.WARNING);
        JButton disable = AppTheme.button("Disable", AppTheme.DANGER);
        JButton clear = AppTheme.ghostButton("Clear");
        JPanel buttons = new JPanel(new GridLayout(2, 2, 6, 6));
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

        JButton add = AppTheme.button("Add user", AppTheme.SUCCESS);
        JButton lock = AppTheme.button("Lock", AppTheme.DANGER);
        JButton unlock = AppTheme.button("Unlock", AppTheme.WARNING);
        JButton clear = AppTheme.ghostButton("Clear");
        JPanel buttons = new JPanel(new GridLayout(2, 2, 6, 6));
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

        JButton add = AppTheme.button("Add", AppTheme.SUCCESS);
        JButton update = AppTheme.button("Update", AppTheme.WARNING);
        JButton disable = AppTheme.button("Disable", AppTheme.DANGER);
        JButton clear = AppTheme.ghostButton("Clear");
        JPanel buttons = new JPanel(new GridLayout(2, 2, 6, 6));
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

    private void refreshMenuModel() {
        menuModel.clear();
        context.menuService.getAllMenu().forEach(menuModel::addElement);
    }

    private void refreshToppingModel() {
        toppingModel.clear();
        context.menuService.getAllToppings().forEach(toppingModel::addElement);
    }

    private void refreshInventoryModel() {
        inventoryModel.clear();
        context.inventoryService.getInventory().forEach(inventoryModel::addElement);
    }

    private void refreshOrderModels() {
        activeOrderModel.clear();
        historyOrderModel.clear();
        context.repository.getOrders().forEach(order -> {
            historyOrderModel.addElement(order);
            if (!"PAID".equals(order.getStatus()) && !"CANCELLED".equals(order.getStatus())) {
                activeOrderModel.addElement(order);
            }
        });
        activeOrderDetailArea.setText("");
        historyOrderDetailArea.setText("");
    }

    private void refreshUserModel() {
        userModel.clear();
        context.userService.getUsers().forEach(userModel::addElement);
    }

    private void fillBeverageForm(MenuItemRecord item) {
        if (item == null) return;
        beverageNameField.setText(item.getName());
        beveragePriceField.setText(String.format("%.0f", item.getBasePrice()));
        categoryBox.setSelectedItem(item.getCategory());
        beverageActiveBox.setSelected(item.isActive());
    }

    private void fillToppingForm(Topping topping) {
        if (topping == null) return;
        toppingNameField.setText(topping.getName());
        toppingPriceField.setText(String.format("%.0f", topping.getExtraPrice()));
        toppingActiveBox.setSelected(topping.isActive());
    }

    private void clearBeverageForm() {
        menuList.clearSelection();
        beverageNameField.setText("");
        beveragePriceField.setText("");
        categoryBox.setSelectedItem("COFFEE");
        beverageActiveBox.setSelected(true);
    }

    private void clearToppingForm() {
        toppingList.clearSelection();
        toppingNameField.setText("");
        toppingPriceField.setText("");
        toppingActiveBox.setSelected(true);
    }

    private void clearUserForm() {
        userList.clearSelection();
        usernameField.setText("");
        passwordField.setText("");
        roleBox.setSelectedItem("CASHIER");
    }

    private long countOrders(String status) {
        return context.repository.getOrders().stream()
                .filter(order -> status.equals(order.getStatus()))
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
}
