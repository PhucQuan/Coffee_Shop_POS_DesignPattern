package com.coffeeshop.presentation;

import com.coffeeshop.AppContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class AppShell {
    private AppShell() {}

    public static JPanel wrap(JFrame owner, AppContext context, String role, String subtitle,
                              JComponent content, String... navItems) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.BG);
        root.add(sidebar(role, navItems), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout(0, 12));
        main.setBackground(AppTheme.BG);
        main.setBorder(new EmptyBorder(16, 16, 16, 16));
        main.add(header(owner, context, role, subtitle), BorderLayout.NORTH);
        main.add(content, BorderLayout.CENTER);
        root.add(main, BorderLayout.CENTER);
        return root;
    }

    private static JPanel header(JFrame owner, AppContext context, String role, String subtitle) {
        JPanel header = AppTheme.card(new BorderLayout(12, 0));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JPanel text = new JPanel(new GridLayout(2, 1, 0, 2));
        text.setOpaque(false);
        JLabel title = new JLabel(role + " Workspace");
        title.setForeground(AppTheme.TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        JLabel sub = AppTheme.muted(subtitle);
        text.add(title);
        text.add(sub);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        JLabel date = AppTheme.muted(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        JButton logout = AppTheme.ghostButton("Logout");
        logout.addActionListener(e -> {
            owner.dispose();
            new LoginView(context).setVisible(true);
        });
        actions.add(date);
        actions.add(logout);

        header.add(text, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private static JPanel sidebar(String role, String[] navItems) {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(176, 0));
        sidebar.setBackground(AppTheme.CREAM);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, AppTheme.BORDER));

        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setBorder(new EmptyBorder(22, 20, 18, 20));
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));

        JLabel app = new JLabel("PurrCoffee");
        app.setForeground(AppTheme.TEXT);
        app.setFont(app.getFont().deriveFont(Font.BOLD, 21f));
        JLabel pos = new JLabel("POS System");
        pos.setForeground(AppTheme.ACCENT);
        pos.setFont(pos.getFont().deriveFont(Font.BOLD, 14f));
        JLabel roleLabel = AppTheme.muted(role);
        roleLabel.setBorder(new EmptyBorder(12, 0, 0, 0));

        brand.add(app);
        brand.add(pos);
        brand.add(roleLabel);
        sidebar.add(brand, BorderLayout.NORTH);

        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setBorder(new EmptyBorder(8, 14, 8, 14));
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        for (int i = 0; i < navItems.length; i++) {
            nav.add(navItem(navItems[i], i == 0));
            nav.add(Box.createVerticalStrut(8));
        }
        sidebar.add(nav, BorderLayout.CENTER);

        JLabel foot = AppTheme.muted("Design Patterns Demo");
        foot.setBorder(new EmptyBorder(0, 20, 22, 20));
        sidebar.add(foot, BorderLayout.SOUTH);
        return sidebar;
    }

    private static JLabel navItem(String text, boolean active) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setForeground(active ? Color.WHITE : AppTheme.TEXT);
        label.setBackground(active ? AppTheme.PRIMARY : AppTheme.CREAM);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 13f));
        label.setBorder(new EmptyBorder(11, 14, 11, 14));
        return label;
    }
}
