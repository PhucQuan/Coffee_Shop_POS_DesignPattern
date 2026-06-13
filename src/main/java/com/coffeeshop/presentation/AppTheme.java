package com.coffeeshop.presentation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class AppTheme {
    public static final Color BG = new Color(246, 241, 233);
    public static final Color PANEL = Color.WHITE;
    public static final Color BORDER = new Color(226, 214, 201);
    public static final Color TEXT = new Color(40, 31, 26);
    public static final Color MUTED = new Color(115, 101, 91);
    public static final Color PRIMARY = new Color(124, 76, 49);
    public static final Color ACCENT = new Color(230, 139, 76);
    public static final Color CREAM = new Color(255, 248, 238);
    public static final Color SUCCESS = new Color(19, 132, 76);
    public static final Color WARNING = new Color(184, 111, 20);
    public static final Color DANGER = new Color(190, 48, 48);
    public static final Color SURFACE = new Color(251, 248, 243);

    private AppTheme() {}

    public static void installLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // The app still works with the platform default look and feel.
        }
        UIManager.put("control", BG);
        UIManager.put("nimbusBase", PRIMARY);
        UIManager.put("nimbusFocus", ACCENT);
        UIManager.put("Button.contentMargins", new Insets(0, 0, 0, 0));
        UIManager.put("ScrollBar.width", 10);
    }

    public static JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 20f));
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel section(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 14f));
        label.setForeground(TEXT);
        return label;
    }

    public static JButton button(String text, Color color) {
        return new RoundedButton(text, color, color.darker(), Color.WHITE);
    }

    public static JButton ghostButton(String text) {
        return new RoundedButton(text, CREAM, new Color(245, 232, 214), TEXT);
    }

    public static void styleField(JTextField field) {
        field.setFont(field.getFont().deriveFont(Font.PLAIN, 15f));
        field.setForeground(TEXT);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(10, 12, 10, 12)
        ));
    }

    public static JLabel muted(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(MUTED);
        return label;
    }

    public static JPanel card(LayoutManager layout) {
        JPanel panel = new RoundedPanel(layout, PANEL, BORDER, 12);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        return panel;
    }

    public static String money(double amount) {
        return String.format("%,.0f d", amount);
    }

    public static JPanel roundedPanel(LayoutManager layout, Color background, Color border, int radius, Insets padding) {
        JPanel panel = new RoundedPanel(layout, background, border, radius);
        panel.setBorder(new EmptyBorder(padding));
        return panel;
    }

    public static JToggleButton toggleButton(String text) {
        JToggleButton button = new RoundedToggleButton(text);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 12f));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(10, 12, 10, 12));
        button.setFocusPainted(false);
        return button;
    }

    private static final class RoundedButton extends JButton {
        private final Color normal;
        private final Color hover;
        private boolean hovered;

        private RoundedButton(String text, Color normal, Color hover, Color foreground) {
            super(text);
            this.normal = normal;
            this.hover = hover;
            setForeground(foreground);
            setFont(getFont().deriveFont(Font.BOLD, 13f));
            setBorder(new EmptyBorder(11, 16, 11, 16));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color fill = isEnabled() ? (hovered ? hover : normal) : new Color(222, 216, 208);
            g.setColor(fill);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            super.paintComponent(graphics);
            g.dispose();
        }
    }

    private static final class RoundedToggleButton extends JToggleButton {
        private RoundedToggleButton(String text) {
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
            Color fill = isSelected() ? PRIMARY : PANEL;
            Color border = isSelected() ? PRIMARY : BORDER;
            g.setColor(fill);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g.setColor(border);
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            setForeground(isSelected() ? Color.WHITE : TEXT);
            super.paintComponent(graphics);
            g.dispose();
        }
    }

    private static final class RoundedPanel extends JPanel {
        private final Color fill;
        private final Color stroke;
        private final int radius;

        private RoundedPanel(LayoutManager layout, Color fill, Color stroke, int radius) {
            super(layout);
            this.fill = fill;
            this.stroke = stroke;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(fill);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            if (stroke != null) {
                g.setColor(stroke);
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            }
            g.dispose();
            super.paintComponent(graphics);
        }
    }
}
