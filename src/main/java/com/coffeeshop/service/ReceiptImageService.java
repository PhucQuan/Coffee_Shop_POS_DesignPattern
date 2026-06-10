package com.coffeeshop.service;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ReceiptImageService {
    private static final int WIDTH = 520;
    private static final int PADDING = 28;
    private static final int LINE_GAP = 6;

    public void saveReceiptPng(String receiptText, File outputFile) {
        if (receiptText == null || receiptText.isBlank()) {
            throw new IllegalArgumentException("Receipt text must not be empty.");
        }
        if (outputFile == null) {
            throw new IllegalArgumentException("Output file must not be null.");
        }

        BufferedImage image = renderReceipt(receiptText);
        File parent = outputFile.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalArgumentException("Cannot create directory: " + parent.getAbsolutePath());
        }
        try {
            ImageIO.write(image, "png", outputFile);
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot write receipt image: " + ex.getMessage(), ex);
        }
    }

    public BufferedImage renderReceipt(String receiptText) {
        String[] lines = receiptText.split("\\R");
        Font font = new Font(Font.MONOSPACED, Font.PLAIN, 16);
        Font titleFont = font.deriveFont(Font.BOLD, 18f);
        BufferedImage probe = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D probeGraphics = probe.createGraphics();
        probeGraphics.setFont(font);
        FontMetrics metrics = probeGraphics.getFontMetrics();
        int lineHeight = metrics.getHeight() + LINE_GAP;
        int qrSize = 126;
        int height = PADDING * 2 + lineHeight * lines.length + qrSize + 58;
        probeGraphics.dispose();

        BufferedImage image = new BufferedImage(WIDTH, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(250, 250, 248));
        g.fillRect(0, 0, WIDTH, height);
        g.setColor(new Color(225, 225, 220));
        g.drawRoundRect(8, 8, WIDTH - 16, height - 16, 16, 16);

        int y = PADDING + metrics.getAscent();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            boolean titleLine = i < 3;
            g.setFont(titleLine ? titleFont : font);
            g.setColor(titleLine ? new Color(28, 74, 120) : new Color(32, 32, 32));
            g.drawString(line, PADDING, y);
            y += lineHeight;
        }
        drawQr(g, receiptText, WIDTH - PADDING - qrSize, y + 8, qrSize);
        g.setColor(new Color(80, 86, 96));
        g.setFont(font.deriveFont(Font.PLAIN, 13f));
        g.drawString("VNPay/Momo demo QR", PADDING, y + 34);
        g.dispose();
        return image;
    }

    private void drawQr(Graphics2D g, String payload, int x, int y, int size) {
        int cell = size / 21;
        g.setColor(Color.WHITE);
        g.fillRect(x - 8, y - 8, size + 16, size + 16);
        g.setColor(Color.BLACK);
        drawFinder(g, x, y, cell);
        drawFinder(g, x + 14 * cell, y, cell);
        drawFinder(g, x, y + 14 * cell, cell);
        int hash = payload.hashCode();
        for (int row = 0; row < 21; row++) {
            for (int col = 0; col < 21; col++) {
                if ((row < 7 && col < 7) || (row < 7 && col >= 14) || (row >= 14 && col < 7)) continue;
                int bit = Integer.rotateLeft(hash, row + col) ^ (row * 31 + col * 17);
                if ((bit & 0b101) == 0) {
                    g.fillRect(x + col * cell, y + row * cell, cell, cell);
                }
            }
        }
    }

    private void drawFinder(Graphics2D g, int x, int y, int cell) {
        g.setColor(Color.BLACK);
        g.fillRect(x, y, 7 * cell, 7 * cell);
        g.setColor(Color.WHITE);
        g.fillRect(x + cell, y + cell, 5 * cell, 5 * cell);
        g.setColor(Color.BLACK);
        g.fillRect(x + 2 * cell, y + 2 * cell, 3 * cell, 3 * cell);
    }
}
