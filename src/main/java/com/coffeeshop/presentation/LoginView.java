package com.coffeeshop.presentation;

import com.coffeeshop.AppContext;
import com.coffeeshop.domain.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginView extends JFrame {
    private final AppContext context;
    private final JTextField usernameField = new JTextField("cashier01", 18);
    private final JPasswordField passwordField = new JPasswordField("123", 18);
    private final JLabel messageLabel = new JLabel(" ");

    public LoginView(AppContext context) {
        this.context = context;
        setTitle("Coffee Shop POS - Login");
        setSize(960, 600);
        setMinimumSize(new Dimension(860, 540));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(content());
        getRootPane().setDefaultButton(null);
    }

    private JPanel content() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.BG);
        root.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel shell = new JPanel(new GridLayout(1, 2, 0, 0));
        shell.setBackground(AppTheme.PANEL);
        shell.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        shell.add(new BrandPanel());
        shell.add(formPanel());

        root.add(shell, BorderLayout.CENTER);
        return root;
    }

    private JPanel formPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(AppTheme.PANEL);
        wrapper.setBorder(new EmptyBorder(56, 56, 56, 56));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;

        JLabel eyebrow = AppTheme.muted("COFFEE SHOP POS");
        eyebrow.setFont(eyebrow.getFont().deriveFont(Font.BOLD, 12f));
        gbc.gridy = 0;
        form.add(eyebrow, gbc);

        JLabel title = new JLabel("Sign in to workspace");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 30f));
        title.setForeground(AppTheme.TEXT);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 8, 0);
        form.add(title, gbc);

        JLabel subtitle = AppTheme.muted("Use one of the demo accounts to open the correct role screen.");
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 24, 0);
        form.add(subtitle, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 6, 0);
        form.add(fieldLabel("Username"), gbc);
        AppTheme.styleField(usernameField);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 16, 0);
        form.add(usernameField, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 6, 0);
        form.add(fieldLabel("Password"), gbc);
        AppTheme.styleField(passwordField);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 8, 0);
        form.add(passwordField, gbc);

        messageLabel.setForeground(AppTheme.DANGER);
        messageLabel.setFont(messageLabel.getFont().deriveFont(Font.BOLD, 12f));
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 14, 0);
        form.add(messageLabel, gbc);

        JButton loginButton = AppTheme.button("Sign in", AppTheme.PRIMARY);
        JButton exitButton = AppTheme.ghostButton("Exit");
        loginButton.addActionListener(e -> login());
        exitButton.addActionListener(e -> dispose());
        passwordField.addActionListener(e -> login());
        usernameField.addActionListener(e -> passwordField.requestFocusInWindow());

        JPanel buttons = new JPanel(new GridLayout(1, 2, 10, 0));
        buttons.setOpaque(false);
        buttons.add(loginButton);
        buttons.add(exitButton);
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 24, 0);
        form.add(buttons, gbc);

        JPanel hints = demoAccountsPanel();
        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, 0, 0);
        form.add(hints, gbc);

        wrapper.add(form, new GridBagConstraints());
        SwingUtilities.invokeLater(usernameField::requestFocusInWindow);
        return wrapper;
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(AppTheme.TEXT);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 13f));
        return label;
    }

    private JPanel demoAccountsPanel() {
        JPanel panel = AppTheme.card(new GridLayout(4, 1, 0, 4));
        JLabel title = new JLabel("Demo accounts");
        title.setForeground(AppTheme.TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 13f));
        panel.add(title);
        panel.add(AppTheme.muted("admin / 123 -> Admin dashboard"));
        panel.add(AppTheme.muted("cashier01 / 123 -> Cashier POS"));
        panel.add(AppTheme.muted("kitchen01 / 123 -> Kitchen board"));
        return panel;
    }

    private void showMessage(String message) {
        messageLabel.setText(message);
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Username and password are required.");
            return;
        }
        showMessage("Checking credentials...");
        context.authService.login(username, password).ifPresentOrElse(this::openByRole,
                () -> showMessage("Invalid username or password."));
    }

    private void openByRole(User user) {
        dispose();
        switch (user.getRole()) {
            case "ADMIN" -> new AdminView(context).setVisible(true);
            case "KITCHEN" -> new KitchenView(context).setVisible(true);
            default -> new POSView(context).setVisible(true);
        }
    }

    private static final class BrandPanel extends JPanel {
        private BrandPanel() {
            setBackground(AppTheme.CREAM);
            setBorder(new EmptyBorder(56, 56, 56, 56));
            setLayout(new BorderLayout());

            JPanel text = new JPanel();
            text.setOpaque(false);
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

            JLabel badge = new JLabel("FINAL PROJECT DEMO");
            badge.setForeground(AppTheme.PRIMARY);
            badge.setFont(badge.getFont().deriveFont(Font.BOLD, 12f));
            text.add(badge);
            text.add(Box.createVerticalStrut(22));

            JLabel brand = new JLabel("PurrCoffee");
            brand.setForeground(AppTheme.TEXT);
            brand.setFont(brand.getFont().deriveFont(Font.BOLD, 42f));
            text.add(brand);

            JLabel pos = new JLabel("Point of Sale");
            pos.setForeground(AppTheme.ACCENT);
            pos.setFont(pos.getFont().deriveFont(Font.BOLD, 26f));
            text.add(pos);
            text.add(Box.createVerticalStrut(18));

            JTextArea copy = new JTextArea("Order fast, send drinks to kitchen, apply discounts, process payment, and review reports from one clean Java app.");
            copy.setOpaque(false);
            copy.setEditable(false);
            copy.setLineWrap(true);
            copy.setWrapStyleWord(true);
            copy.setForeground(AppTheme.MUTED);
            copy.setFont(copy.getFont().deriveFont(Font.PLAIN, 15f));
            copy.setMaximumSize(new Dimension(360, 90));
            text.add(copy);

            add(text, BorderLayout.NORTH);
            add(new CoffeeHero(), BorderLayout.CENTER);
        }
    }

    private static final class CoffeeHero extends JPanel {
        private CoffeeHero() {
            setOpaque(false);
            setPreferredSize(new Dimension(300, 260));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int cx = w / 2;
            int cy = h / 2 + 20;

            g.setColor(new Color(236, 220, 203));
            g.fillOval(cx - 120, cy + 68, 240, 34);

            g.setColor(Color.WHITE);
            g.fillRoundRect(cx - 96, cy - 78, 192, 150, 40, 40);
            g.setColor(AppTheme.BORDER);
            g.setStroke(new BasicStroke(3f));
            g.drawRoundRect(cx - 96, cy - 78, 192, 150, 40, 40);

            g.setColor(new Color(112, 63, 37));
            g.fillOval(cx - 72, cy - 54, 144, 58);
            g.setColor(new Color(238, 204, 162));
            g.fillOval(cx - 50, cy - 42, 100, 34);
            g.setColor(new Color(255, 245, 226, 150));
            g.setStroke(new BasicStroke(4f));
            g.drawArc(cx - 36, cy - 38, 72, 28, 15, 280);

            g.setColor(new Color(250, 250, 250));
            g.setStroke(new BasicStroke(10f));
            g.drawArc(cx + 82, cy - 38, 58, 74, -70, 250);
            g.setColor(AppTheme.BORDER);
            g.setStroke(new BasicStroke(3f));
            g.drawArc(cx + 82, cy - 38, 58, 74, -70, 250);

            g.setColor(AppTheme.ACCENT);
            g.fillRoundRect(cx - 86, cy + 94, 172, 18, 18, 18);

            g.setColor(new Color(117, 73, 42, 60));
            for (int i = 0; i < 3; i++) {
                int x = cx - 48 + i * 48;
                g.setStroke(new BasicStroke(3f));
                g.drawArc(x, cy - 132, 30, 72, 70, 80);
            }
            g.dispose();
        }
    }
}
