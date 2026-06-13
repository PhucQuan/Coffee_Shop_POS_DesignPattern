package com.coffeeshop.presentation;

import com.coffeeshop.AppContext;
import com.coffeeshop.domain.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;

public class LoginView extends JFrame {
    private final AppContext context;
    private final JTextField usernameField = new JTextField("cashier01", 18);
    private final JPasswordField passwordField = new JPasswordField("123", 18);
    private final JLabel messageLabel = new JLabel(" ");

    public LoginView(AppContext context) {
        this.context = context;
        setTitle("Coffee Shop POS - Login");
        setSize(980, 620);
        setMinimumSize(new Dimension(900, 560));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(content());
        getRootPane().setDefaultButton(null);
    }

    private JPanel content() {
        JPanel root = new HeroLoginPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(34, 54, 34, 54));
        root.add(formPanel(), BorderLayout.WEST);
        return root;
    }

    private JPanel formPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.setPreferredSize(new Dimension(470, 0));
        wrapper.setBorder(new EmptyBorder(36, 18, 36, 18));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;

        JLabel eyebrow = AppTheme.muted("COFFEE SHOP POS");
        eyebrow.setForeground(AppTheme.PRIMARY);
        eyebrow.setFont(eyebrow.getFont().deriveFont(Font.BOLD, 13f));
        gbc.gridy = 0;
        form.add(eyebrow, gbc);

        JLabel title = new JLabel("PurrCoffee");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 48f));
        title.setForeground(AppTheme.TEXT);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 8, 0);
        form.add(title, gbc);

        JLabel subtitle = new JLabel("Sign in to run cashier, kitchen, and admin workflows.");
        subtitle.setForeground(AppTheme.MUTED);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 26, 0);
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

    private static final class HeroLoginPanel extends JPanel {
        private final Image background;

        private HeroLoginPanel(LayoutManager layout) {
            super(layout);
            URL url = LoginView.class.getResource("/assets/backgrounds/coffee-hero.jpg");
            background = url == null ? null : new ImageIcon(url).getImage();
            setBackground(new Color(255, 252, 247));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (background != null) {
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }
            GradientPaint veil = new GradientPaint(0, 0, new Color(255, 252, 247, 245),
                    getWidth(), 0, new Color(255, 252, 247, 40));
            g.setPaint(veil);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(new Color(255, 255, 255, 210));
            g.fillRoundRect(34, 34, 500, getHeight() - 68, 24, 24);
            g.setColor(new Color(226, 214, 201, 160));
            g.drawRoundRect(34, 34, 500, getHeight() - 68, 24, 24);
            g.setColor(new Color(124, 76, 49, 24));
            for (int i = 0; i < 5; i++) {
                int y = 90 + i * 72;
                g.fillOval(720 + i * 18, y, 120, 120);
            }
            g.dispose();
        }
    }
}
