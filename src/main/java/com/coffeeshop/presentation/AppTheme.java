package com.coffeeshop.presentation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

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
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 13f));
        button.setBorder(new EmptyBorder(10, 14, 10, 14));
        return button;
    }

    public static JButton ghostButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setForeground(TEXT);
        button.setBackground(CREAM);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 13f));
        button.setBorder(new EmptyBorder(9, 12, 9, 12));
        return button;
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
        JPanel panel = new JPanel(layout);
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(12, 12, 12, 12)
        ));
        return panel;
    }

    public static String money(double amount) {
        return String.format("%,.0f d", amount);
    }
}
