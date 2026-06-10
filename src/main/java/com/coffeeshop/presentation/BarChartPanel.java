package com.coffeeshop.presentation;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class BarChartPanel extends JPanel {
    private Map<String, Long> data = new LinkedHashMap<>();

    public BarChartPanel() {
        setPreferredSize(new Dimension(420, 240));
        setBorder(BorderFactory.createTitledBorder("Top selling items"));
        setBackground(Color.WHITE);
    }

    public void setData(Map<String, Long> data) {
        this.data = data == null ? new LinkedHashMap<>() : new LinkedHashMap<>(data);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int width = getWidth() - 50;
        int x = 24;
        int y = 42;
        int barHeight = 24;
        int gap = 16;
        long max = data.values().stream().mapToLong(Long::longValue).max().orElse(1);
        if (data.isEmpty()) {
            g.setColor(Color.DARK_GRAY);
            g.drawString("No paid orders yet.", x, y);
            g.dispose();
            return;
        }
        int index = 0;
        for (Map.Entry<String, Long> entry : data.entrySet()) {
            if (index >= 5) break;
            int currentY = y + index * (barHeight + gap);
            int barWidth = (int) Math.max(8, (width - 180) * (entry.getValue() / (double) max));
            g.setColor(new Color(46, 116, 181));
            g.fillRoundRect(x + 150, currentY - 16, barWidth, barHeight, 8, 8);
            g.setColor(Color.DARK_GRAY);
            g.drawString(shorten(entry.getKey()), x, currentY);
            g.drawString(String.valueOf(entry.getValue()), x + 158 + barWidth, currentY);
            index++;
        }
        g.dispose();
    }

    private String shorten(String value) {
        return value.length() <= 20 ? value : value.substring(0, 17) + "...";
    }
}
