package com.coffeeshop.presentation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Design System — PurrCoffee POS
 *
 * Palette  : Warm Amber × Coffee Brown × Honey Gold   (ui-ux-pro-max)
 * Style    : Soft elevated cards, no border+shadow pairing  (impeccable)
 * Spacing  : 8-pt grid, 14 px max card radius              (frontend-ui-engineering)
 * Contrast : ≥ 4.5:1 for all body text                     (frontend-ui-engineering)
 */
public final class AppTheme {

    // ── Palette ──────────────────────────────────────────────────────────────
    /** Page background — very light warm amber */
    public static final Color BG      = new Color(254, 248, 232);   // #FEF8E8
    /** Surface for scrollable lists / alternate rows */
    public static final Color SURFACE = new Color(255, 253, 247);   // #FFFDF7
    /** Card background — pure white */
    public static final Color PANEL   = new Color(255, 255, 255);
    /** Divider / subtle outline */
    public static final Color BORDER  = new Color(232, 220, 198);   // #E8DCC6

    /** Primary ink — deep espresso brown, contrast > 10:1 on BG */
    public static final Color TEXT    = new Color(42, 18, 4);       // #2A1204
    /** Secondary ink — warm mid-brown, contrast > 4.5:1 on BG */
    public static final Color MUTED   = new Color(109, 76, 46);     // #6D4C2E

    /** Brand primary — warm copper (button fills, accents) */
    public static final Color PRIMARY = new Color(180, 68, 12);     // #B4440C
    /** Brand accent — honey gold (prices, highlights) */
    public static final Color ACCENT  = new Color(202, 138, 4);     // #CA8A04

    // Sidebar-specific palette
    /** Sidebar bg — medium coffee brown (not jet-black, not cream) */
    public static final Color SIDEBAR_BG       = new Color(62, 32, 10);   // #3E200A
    public static final Color SIDEBAR_TEXT     = new Color(228, 212, 188); // #E4D4BC
    public static final Color SIDEBAR_MUTED    = new Color(148, 122, 94);  // #947A5E
    public static final Color SIDEBAR_HOVER_BG = new Color(84, 46, 16);   // #542E10

    // Semantic colours
    public static final Color SUCCESS = new Color(22, 148, 88);   // #169458
    public static final Color WARNING = new Color(180, 112, 8);   // #B47008
    public static final Color DANGER  = new Color(192, 38, 38);   // #C02626

    // Legacy alias
    public static final Color CREAM = SURFACE;

    private static final String FONT = "Segoe UI";

    private AppTheme() {}

    // ── Look & Feel ──────────────────────────────────────────────────────────

    public static void installLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        setGlobalFont(new Font(FONT, Font.PLAIN, 13));
        // Apply palette to native Swing widgets
        UIManager.put("Panel.background",               BG);
        UIManager.put("ScrollPane.background",          SURFACE);
        UIManager.put("Viewport.background",            SURFACE);
        UIManager.put("List.background",                SURFACE);
        UIManager.put("List.selectionBackground",       new Color(254, 243, 213));
        UIManager.put("List.selectionForeground",       TEXT);
        UIManager.put("SplitPane.background",           BG);
        UIManager.put("SplitPaneDivider.background",    BORDER);
        UIManager.put("TabbedPane.background",          BG);
        UIManager.put("TabbedPane.selected",            PANEL);
        UIManager.put("TabbedPane.foreground",          TEXT);
        UIManager.put("TabbedPane.selectHighlight",     PRIMARY);
        UIManager.put("ScrollBar.thumb",                BORDER);
        UIManager.put("ScrollBar.track",                BG);
        UIManager.put("ScrollBar.width",                8);
        UIManager.put("ComboBox.background",            PANEL);
        UIManager.put("ComboBox.selectionBackground",   new Color(254, 243, 213));
        UIManager.put("ComboBox.foreground",            TEXT);
        UIManager.put("TextArea.background",            PANEL);
        UIManager.put("TextArea.foreground",            TEXT);
        UIManager.put("TextField.background",           PANEL);
        UIManager.put("TextField.foreground",           TEXT);
        UIManager.put("TextField.caretForeground",      PRIMARY);
    }

    private static void setGlobalFont(Font font) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key   = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put(key, new javax.swing.plaf.FontUIResource(font));
        }
    }

    // ── Label helpers ────────────────────────────────────────────────────────

    public static JLabel title(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(FONT, Font.BOLD, 22));
        l.setForeground(TEXT);
        return l;
    }

    public static JLabel section(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(FONT, Font.BOLD, 15));
        l.setForeground(TEXT);
        return l;
    }

    public static JLabel muted(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(MUTED);
        l.setFont(new Font(FONT, Font.PLAIN, 12));
        return l;
    }

    // ── Button helpers ───────────────────────────────────────────────────────

    /** Filled brand button */
    public static JButton button(String text, Color color) {
        return new PrimaryButton(text, color);
    }

    /** Subtle outline button — uses border ONLY (no shadow), per impeccable rules */
    public static JButton ghostButton(String text) {
        return new GhostButton(text);
    }

    // ── Field helper ─────────────────────────────────────────────────────────

    public static void styleField(JTextField field) {
        field.setFont(new Font(FONT, Font.PLAIN, 14));
        field.setForeground(TEXT);
        field.setBackground(PANEL);
        field.setCaretColor(PRIMARY);
        // Border only — no additional shadow on form fields (impeccable rule)
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(10, 12, 10, 12)));
    }

    // ── Panel helpers ────────────────────────────────────────────────────────

    /**
     * Standard card — white, single soft shadow, 14 px radius.
     * No border paired with shadow (impeccable: pick one).
     */
    public static JPanel card(LayoutManager layout) {
        ShadowCard p = new ShadowCard(layout, PANEL, 14);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));
        return p;
    }

    /**
     * Generic rounded panel factory used by views.
     * border == null → shadow card; border != null → bordered panel (no shadow).
     */
    public static JPanel roundedPanel(LayoutManager layout, Color bg,
                                      Color border, int radius, Insets padding) {
        JPanel p;
        if (border == null) {
            p = new ShadowCard(layout, bg, Math.min(radius, 16));
        } else {
            p = new BorderPanel(layout, bg, border, Math.min(radius, 14));
        }
        p.setBorder(new EmptyBorder(padding));
        return p;
    }

    /** Category / filter pill toggle */
    public static JToggleButton toggleButton(String text) {
        PillToggle b = new PillToggle(text);
        b.setFont(new Font(FONT, Font.BOLD, 12));
        b.setHorizontalAlignment(SwingConstants.CENTER);
        b.setBorder(new EmptyBorder(9, 14, 9, 14));
        b.setFocusPainted(false);
        return b;
    }

    public static String money(double amount) {
        return String.format("%,.0f \u0111", amount);
    }

    // ── Sub-components ───────────────────────────────────────────────────────

    /**
     * Elevated white card with a single multi-layer shadow.
     * Shadow only — never paired with a visible border (impeccable).
     */
    static final class ShadowCard extends JPanel {
        private final Color bg;
        private final int r;

        ShadowCard(LayoutManager layout, Color bg, int r) {
            super(layout);
            this.bg = bg;
            this.r  = r;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                               RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            // Layer 1 — ambient shadow (spread)
            g.setColor(new Color(62, 32, 10, 9));
            g.fillRoundRect(1, 4, w - 2, h - 2, r + 2, r + 2);
            // Layer 2 — directional shadow
            g.setColor(new Color(62, 32, 10, 14));
            g.fillRoundRect(1, 2, w - 2, h - 2, r + 2, r + 2);
            // Card surface
            g.setColor(bg);
            g.fillRoundRect(0, 0, w - 1, h - 5, r, r);
            g.dispose();
            super.paintComponent(g0);
        }
    }

    /**
     * Bordered panel — thin line only, NO shadow paired with it (impeccable).
     */
    static final class BorderPanel extends JPanel {
        private final Color bg, stroke;
        private final int r;

        BorderPanel(LayoutManager layout, Color bg, Color stroke, int r) {
            super(layout);
            this.bg     = bg;
            this.stroke = stroke;
            this.r      = r;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                               RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(bg);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, r, r);
            g.setColor(stroke);
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, r, r);
            g.dispose();
            super.paintComponent(g0);
        }
    }

    /**
     * Filled brand button with hover brightness shift.
     * Radius 10 px — consistent with card hierarchy (impeccable).
     */
    private static final class PrimaryButton extends JButton {
        private final Color base;
        private boolean pressed, hovered;

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
                public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                public void mousePressed(MouseEvent e) { pressed = true;  repaint(); }
                public void mouseReleased(MouseEvent e){ pressed = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                               RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            if (!isEnabled()) {
                g.setColor(new Color(208, 195, 176));
                g.fillRoundRect(0, 0, w, h, 10, 10);
                setForeground(new Color(160, 145, 126));
            } else {
                Color fill = pressed ? base.darker()
                           : hovered ? base.brighter()
                           : base;
                // Single subtle shadow — no double decoration
                g.setColor(new Color(62, 32, 10, 22));
                g.fillRoundRect(0, 2, w, h, 10, 10);
                g.setColor(fill);
                g.fillRoundRect(0, 0, w, h - 2, 10, 10);
                // Subtle inner highlight — warmth
                g.setColor(new Color(255, 255, 255, 24));
                g.fillRoundRect(2, 2, w - 4, (h - 4) / 2, 8, 8);
                setForeground(Color.WHITE);
            }
            super.paintComponent(g0);
            g.dispose();
        }
    }

    /**
     * Ghost button — border only, no fill, no shadow (impeccable rule).
     */
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
                public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                               RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            if (!isEnabled()) {
                g.setColor(new Color(210, 200, 185, 80));
                g.fillRoundRect(0, 0, w - 1, h - 1, 10, 10);
                g.setColor(BORDER);
                g.drawRoundRect(0, 0, w - 1, h - 1, 10, 10);
                setForeground(new Color(180, 164, 142));
            } else {
                Color fill   = hovered ? new Color(232, 215, 185, 120) : new Color(0,0,0,0);
                Color stroke = hovered ? new Color(180, 140, 90)       : BORDER;
                g.setColor(fill);
                g.fillRoundRect(0, 0, w - 1, h - 1, 10, 10);
                g.setColor(stroke);
                g.drawRoundRect(0, 0, w - 1, h - 1, 10, 10);
                setForeground(hovered ? TEXT : MUTED);
            }
            super.paintComponent(g0);
            g.dispose();
        }
    }

    /**
     * Category pill toggle — selected = honey-gold fill.
     */
    private static final class PillToggle extends JToggleButton {
        PillToggle(String text) {
            super(text);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                               RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            if (isSelected()) {
                // Warm honey-amber fill — no border paired with filled state
                g.setColor(new Color(62, 32, 10, 22));
                g.fillRoundRect(0, 2, w, h, 12, 12);
                g.setColor(PRIMARY);
                g.fillRoundRect(0, 0, w, h - 2, 12, 12);
                setForeground(Color.WHITE);
            } else {
                g.setColor(new Color(232, 215, 185, 60));
                g.fillRoundRect(0, 0, w - 1, h - 1, 12, 12);
                g.setColor(BORDER);
                g.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);
                setForeground(MUTED);
            }
            super.paintComponent(g0);
            g.dispose();
        }
    }
}
