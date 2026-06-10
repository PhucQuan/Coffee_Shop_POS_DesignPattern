package com.coffeeshop.presentation;

import javax.swing.*;
import java.awt.*;

public class QrCodePanel extends JPanel {
    private final String payload;

    public QrCodePanel(String payload) {
        this.payload = payload == null ? "COFFEE_SHOP_POS" : payload;
        setPreferredSize(new Dimension(180, 210));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Payment QR"));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        int size = 132;
        int cell = size / 21;
        int startX = (getWidth() - size) / 2;
        int startY = 28;
        g.setColor(Color.WHITE);
        g.fillRect(startX, startY, size, size);
        g.setColor(Color.BLACK);

        drawFinder(g, startX, startY, cell);
        drawFinder(g, startX + 14 * cell, startY, cell);
        drawFinder(g, startX, startY + 14 * cell, cell);

        int hash = payload.hashCode();
        for (int row = 0; row < 21; row++) {
            for (int col = 0; col < 21; col++) {
                if (isFinderArea(row, col)) continue;
                int bit = Integer.rotateLeft(hash, row + col) ^ (row * 31 + col * 17);
                if ((bit & 0b101) == 0) {
                    g.fillRect(startX + col * cell, startY + row * cell, cell, cell);
                }
            }
        }

        g.setColor(AppTheme.MUTED);
        g.setFont(g.getFont().deriveFont(Font.PLAIN, 11f));
        g.drawString("VNPay/Momo demo QR", 28, startY + size + 24);
        g.dispose();
    }

    private void drawFinder(Graphics2D g, int x, int y, int cell) {
        g.fillRect(x, y, 7 * cell, 7 * cell);
        g.setColor(Color.WHITE);
        g.fillRect(x + cell, y + cell, 5 * cell, 5 * cell);
        g.setColor(Color.BLACK);
        g.fillRect(x + 2 * cell, y + 2 * cell, 3 * cell, 3 * cell);
    }

    private boolean isFinderArea(int row, int col) {
        return (row < 7 && col < 7) || (row < 7 && col >= 14) || (row >= 14 && col < 7);
    }
}
