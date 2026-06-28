package com.coffeeshop.presentation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class AppTheme {
    public static final Color BG = new Color(247, 241, 235);
    public static final Color SURFACE = new Color(255, 252, 248);
    public static final Color PANEL = new Color(255, 254, 252);
    public static final Color BORDER = new Color(229, 218, 208);

    public static final Color TEXT = new Color(75, 52, 39);
    public static final Color MUTED = new Color(138, 118, 104);

    public static final Color PRIMARY = new Color(194, 145, 122);
    public static final Color ACCENT = new Color(163, 187, 170);
    public static final Color TINT = new Color(245, 232, 223);
    public static final Color DETAIL_PANEL = new Color(248, 239, 232);
    public static final Color DETAIL_BORDER = new Color(232, 216, 207);

    public static final Color SIDEBAR_BG = new Color(151, 125, 117);
    public static final Color SIDEBAR_TEXT = new Color(252, 247, 242);
    public static final Color SIDEBAR_MUTED = new Color(242, 230, 223);
    public static final Color SIDEBAR_HOVER_BG = new Color(172, 149, 140);

    public static final Color SUCCESS = new Color(126, 163, 140);
    public static final Color WARNING = new Color(214, 170, 126);
    public static final Color DANGER = new Color(192, 114, 108);

    public static final Color CREAM = SURFACE;

    private static final String FONT = "Segoe UI";

    private AppTheme() {}

    public static void installLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        setGlobalFont(new Font(FONT, Font.PLAIN, 13));
        UIManager.put("Panel.background", BG);
        UIManager.put("ScrollPane.background", SURFACE);
        UIManager.put("Viewport.background", SURFACE);
        UIManager.put("List.background", SURFACE);
        UIManager.put("List.selectionBackground", TINT);
        UIManager.put("List.selectionForeground", TEXT);
        UIManager.put("SplitPane.background", BG);
        UIManager.put("SplitPaneDivider.background", BORDER);
        UIManager.put("TabbedPane.background", BG);
        UIManager.put("TabbedPane.selected", PANEL);
        UIManager.put("TabbedPane.foreground", TEXT);
        UIManager.put("TabbedPane.selectHighlight", PRIMARY);
        UIManager.put("ScrollBar.thumb", BORDER);
        UIManager.put("ScrollBar.track", BG);
        UIManager.put("ScrollBar.width", 8);
        UIManager.put("ComboBox.background", PANEL);
        UIManager.put("ComboBox.selectionBackground", TINT);
        UIManager.put("ComboBox.foreground", TEXT);
        UIManager.put("TextArea.background", PANEL);
        UIManager.put("TextArea.foreground", TEXT);
        UIManager.put("TextField.background", PANEL);
        UIManager.put("TextField.foreground", TEXT);
        UIManager.put("TextField.caretForeground", TEXT);
    }

    private static void setGlobalFont(Font font) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, new javax.swing.plaf.FontUIResource(font));
            }
        }
    }

    public static JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(FONT, Font.BOLD, 22));
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel section(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(FONT, Font.BOLD, 15));
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel muted(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(MUTED);
        label.setFont(new Font(FONT, Font.PLAIN, 12));
        return label;
    }

    public static JButton button(String text, Color color) {
        return new PrimaryButton(text, color);
    }

    public static JButton ghostButton(String text) {
        return new GhostButton(text);
    }

    public static void styleField(JTextField field) {
        field.setFont(new Font(FONT, Font.PLAIN, 14));
        field.setForeground(TEXT);
        field.setBackground(PANEL);
        field.setCaretColor(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(10, 12, 10, 12)));
    }

    public static JPanel card(LayoutManager layout) {
        ShadowCard panel = new ShadowCard(layout, PANEL, 16);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        return panel;
    }

    public static JPanel roundedPanel(LayoutManager layout, Color bg, Color border, int radius, Insets padding) {
        JPanel panel = border == null
                ? new ShadowCard(layout, bg, Math.min(radius, 18))
                : new BorderPanel(layout, bg, border, Math.min(radius, 18));
        panel.setBorder(new EmptyBorder(padding));
        return panel;
    }

    public static JToggleButton toggleButton(String text) {
        PillToggle button = new PillToggle(text);
        button.setFont(new Font(FONT, Font.BOLD, 12));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setBorder(new EmptyBorder(10, 14, 10, 14));
        button.setFocusPainted(false);
        return button;
    }

    public static String money(double amount) {
        return String.format("%,.0f \u0111", amount);
    }

    static final class ShadowCard extends JPanel {
        private final Color bg;
        private final int radius;

        ShadowCard(LayoutManager layout, Color bg, int radius) {
            super(layout);
            this.bg = bg;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            g.setColor(new Color(75, 52, 39, 8));
            g.fillRoundRect(2, 6, w - 4, h - 4, radius + 2, radius + 2);
            g.setColor(new Color(75, 52, 39, 12));
            g.fillRoundRect(2, 3, w - 4, h - 5, radius + 2, radius + 2);
            g.setColor(bg);
            g.fillRoundRect(0, 0, w - 1, h - 6, radius, radius);
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    static final class BorderPanel extends JPanel {
        private final Color bg;
        private final Color stroke;
        private final int radius;

        BorderPanel(LayoutManager layout, Color bg, Color stroke, int radius) {
            super(layout);
            this.bg = bg;
            this.stroke = stroke;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(bg);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g.setColor(stroke);
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private static final class PrimaryButton extends JButton {
        private final Color base;
        private boolean pressed;
        private boolean hovered;

        PrimaryButton(String text, Color base) {
            super(text);
            this.base = base;
            setFont(new Font(FONT, Font.BOLD, 13));
            setForeground(Color.WHITE);
            setBorder(new EmptyBorder(10, 18, 10, 18));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
                public void mousePressed(MouseEvent e) { pressed = true; repaint(); }
                public void mouseReleased(MouseEvent e) { pressed = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            if (!isEnabled()) {
                g.setColor(new Color(214, 202, 194));
                g.fillRoundRect(0, 0, w, h, 12, 12);
                setForeground(new Color(164, 148, 138));
            } else {
                Color fill = pressed ? base.darker() : hovered ? base.brighter() : base;
                g.setColor(new Color(75, 52, 39, 16));
                g.fillRoundRect(0, 3, w, h - 1, 12, 12);
                g.setColor(fill);
                g.fillRoundRect(0, 0, w, h - 3, 12, 12);
                g.setColor(new Color(255, 255, 255, 34));
                g.fillRoundRect(2, 2, w - 4, Math.max(8, (h - 8) / 2), 10, 10);
                setForeground(Color.WHITE);
            }
            super.paintComponent(graphics);
            g.dispose();
        }
    }

    private static final class GhostButton extends JButton {
        private boolean hovered;

        GhostButton(String text) {
            super(text);
            setFont(new Font(FONT, Font.PLAIN, 13));
            setForeground(MUTED);
            setBorder(new EmptyBorder(10, 16, 10, 16));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            if (!isEnabled()) {
                g.setColor(new Color(227, 218, 208, 110));
                g.fillRoundRect(0, 0, w - 1, h - 1, 12, 12);
                g.setColor(BORDER);
                g.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);
                setForeground(new Color(169, 153, 142));
            } else {
                Color fill = hovered ? new Color(TINT.getRed(), TINT.getGreen(), TINT.getBlue(), 150) : new Color(0, 0, 0, 0);
                Color stroke = hovered ? PRIMARY : BORDER;
                g.setColor(fill);
                g.fillRoundRect(0, 0, w - 1, h - 1, 12, 12);
                g.setColor(stroke);
                g.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);
                setForeground(hovered ? TEXT : MUTED);
            }
            super.paintComponent(graphics);
            g.dispose();
        }
    }

    private static final class PillToggle extends JToggleButton {
        PillToggle(String text) {
            super(text);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            if (isSelected()) {
                g.setColor(new Color(75, 52, 39, 14));
                g.fillRoundRect(0, 3, w, h - 1, 14, 14);
                g.setColor(PRIMARY);
                g.fillRoundRect(0, 0, w, h - 3, 14, 14);
                setForeground(Color.WHITE);
            } else {
                g.setColor(new Color(TINT.getRed(), TINT.getGreen(), TINT.getBlue(), 110));
                g.fillRoundRect(0, 0, w - 1, h - 1, 14, 14);
                g.setColor(BORDER);
                g.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);
                setForeground(MUTED);
            }
            super.paintComponent(graphics);
            g.dispose();
        }
    }
}
