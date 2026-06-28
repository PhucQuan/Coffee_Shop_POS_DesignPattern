package com.coffeeshop.presentation;

import com.coffeeshop.AppContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * AppShell — outer chrome: sidebar + header + content area.
 *
 * Sidebar design:
 *   - Medium coffee-brown (#3E200A) — warm, not jet-black, not cream.
 *   - Active nav: left accent bar + warm tint (frontend-ui-engineering active states).
 *   - No emoji icons — text + geometry only (impeccable rule).
 *
 * Header:
 *   - Borderless, lives in the warm-amber BG area.
 *   - Bold role title + muted subtitle — clear hierarchy (frontend-ui-engineering).
 */
public final class AppShell {
    private AppShell() {}

    public static JPanel wrap(JFrame owner, AppContext context, String role, String subtitle,
                              JComponent content, String... navItems) {
        return wrap(owner, context, role, subtitle, content, null, navItems);
    }

    public static JPanel wrap(JFrame owner, AppContext context, String role, String subtitle,
                              JComponent content, Consumer<String> navHandler, String... navItems) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.BG);
        JPanel sidebar = sidebar(owner, context, role, navHandler, navItems);

        JPanel main = new JPanel(new BorderLayout(0, 14));
        main.setBackground(AppTheme.BG);
        main.setBorder(new EmptyBorder(20, 20, 20, 20));
        main.add(header(role, subtitle), BorderLayout.NORTH);
        main.add(content, BorderLayout.CENTER);

        JSplitPane shellSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, main);
        shellSplit.setBorder(null);
        shellSplit.setDividerSize(6);
        shellSplit.setContinuousLayout(true);
        shellSplit.setOneTouchExpandable(false);
        shellSplit.setResizeWeight(0);
        shellSplit.setDividerLocation(196);
        shellSplit.setBackground(AppTheme.BG);
        root.add(shellSplit, BorderLayout.CENTER);
        return root;
    }

    // ── Header — borderless, clean ────────────────────────────────────────────

    private static JPanel header(String role, String subtitle) {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 2, 8, 2));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(role + " Workspace");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(AppTheme.TEXT);

        text.add(title);
        if (subtitle != null && !subtitle.isBlank()) {
            JLabel sub = new JLabel(subtitle);
            sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            sub.setForeground(AppTheme.MUTED);
            text.add(Box.createVerticalStrut(3));
            text.add(sub);
        }

        // Date — right side, no logout button in header (moved to sidebar bottom)
        JLabel date = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        date.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        date.setForeground(AppTheme.MUTED);

        header.add(text, BorderLayout.WEST);
        header.add(date, BorderLayout.EAST);
        return header;
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────

    private static JPanel sidebar(JFrame owner, AppContext context, String role,
                                  Consumer<String> navHandler, String[] navItems) {
        WarmSidebar sidebar = new WarmSidebar();
        sidebar.setPreferredSize(new Dimension(196, 0));
        sidebar.setLayout(new BorderLayout());

        // ── Brand block ───────────────────────────────────────────────────────
        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBorder(new EmptyBorder(26, 20, 20, 20));

        // App name
        JLabel appName = new JLabel("PurrCoffee");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appName.setForeground(AppTheme.SIDEBAR_TEXT);
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Role pill — small coloured label
        JLabel roleLbl = new JLabel(role.toUpperCase());
        roleLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        roleLbl.setForeground(AppTheme.SIDEBAR_MUTED);
        roleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        roleLbl.setBorder(new EmptyBorder(5, 0, 0, 0));

        brand.add(appName);
        brand.add(roleLbl);
        sidebar.add(brand, BorderLayout.NORTH);

        // ── Nav items ─────────────────────────────────────────────────────────
        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(new EmptyBorder(0, 10, 8, 10));
        List<SideNavButton> navButtons = new ArrayList<>();
        for (int i = 0; i < navItems.length; i++) {
            SideNavButton button = new SideNavButton(navItems[i], i == 0);
            button.setEnabled(navHandler != null);
            if (navHandler != null) {
                button.addActionListener(e -> {
                    setActive(navButtons, button);
                    navHandler.accept(button.getText());
                });
            }
            navButtons.add(button);
            nav.add(button);
            nav.add(Box.createVerticalStrut(3));
        }
        sidebar.add(nav, BorderLayout.CENTER);

        // ── Sidebar bottom ────────────────────────────────────────────────────
        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBorder(new EmptyBorder(0, 10, 22, 10));

        // Thin warm divider
        JPanel divider = new JPanel();
        divider.setOpaque(true);
        divider.setBackground(new Color(255, 255, 255, 40));
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setAlignmentX(Component.LEFT_ALIGNMENT);

        bottom.add(divider);
        bottom.add(Box.createVerticalStrut(10));
        bottom.add(navBtn("Logout", false, ignored -> {
            owner.dispose();
            new LoginView(context).setVisible(true);
        }));

        JLabel foot = new JLabel("Design Patterns POS");
        foot.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        foot.setForeground(AppTheme.SIDEBAR_MUTED);
        foot.setAlignmentX(Component.LEFT_ALIGNMENT);
        foot.setBorder(new EmptyBorder(8, 10, 0, 0));
        bottom.add(foot);

        sidebar.add(bottom, BorderLayout.SOUTH);
        return sidebar;
    }

    private static JButton navBtn(String text, boolean active, Consumer<String> handler) {
        SideNavButton btn = new SideNavButton(text, active);
        btn.setEnabled(handler != null);
        if (handler != null) btn.addActionListener(e -> handler.accept(text));
        return btn;
    }

    private static void setActive(List<SideNavButton> buttons, SideNavButton activeButton) {
        for (SideNavButton button : buttons) {
            button.setActive(button == activeButton);
        }
    }

    // ── Custom painted sidebar ────────────────────────────────────────────────

    /** Warm coffee-brown sidebar with very subtle gradient */
    private static final class WarmSidebar extends JPanel {
        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            // Vertical gradient: slightly lighter at top (warmth)
            GradientPaint gp = new GradientPaint(
                    0, 0, AppTheme.SIDEBAR_HOVER_BG,
                    0, getHeight(), AppTheme.SIDEBAR_BG);
            g.setPaint(gp);
            g.fillRect(0, 0, getWidth(), getHeight());
            // 1 px right-edge separator (no wide decorative border)
            g.setColor(new Color(255, 255, 255, 32));
            g.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
            g.dispose();
        }
    }

    /** Sidebar navigation button */
    private static final class SideNavButton extends JButton {
        private boolean active;
        private boolean hovered;

        SideNavButton(String text, boolean active) {
            super(text);
            this.active = active;
            setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 13));
            setForeground(AppTheme.SIDEBAR_TEXT);
            setBorder(new EmptyBorder(10, 14, 10, 14));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setHorizontalAlignment(SwingConstants.LEFT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
            });
        }

        private void setActive(boolean active) {
            this.active = active;
            setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 13));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                               RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();

            if (active) {
                // Warm tinted bg + 3px left accent bar
                g.setColor(new Color(255, 255, 255, 34));
                g.fillRoundRect(0, 0, w, h, 8, 8);
                // Accent bar — primary copper (3px)
                g.setColor(AppTheme.PRIMARY);
                g.fillRoundRect(0, 4, 3, h - 8, 3, 3);
                setForeground(AppTheme.SIDEBAR_TEXT);
            } else if (hovered && isEnabled()) {
                g.setColor(new Color(255, 255, 255, 16));
                g.fillRoundRect(0, 0, w, h, 8, 8);
                setForeground(AppTheme.SIDEBAR_TEXT);
            } else {
                setForeground(AppTheme.SIDEBAR_TEXT);
            }
            super.paintComponent(g0);
            g.dispose();
        }
    }
}
