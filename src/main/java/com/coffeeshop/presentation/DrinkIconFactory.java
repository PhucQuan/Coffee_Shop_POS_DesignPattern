package com.coffeeshop.presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.text.Normalizer;
import java.util.Locale;

public final class DrinkIconFactory {
    private DrinkIconFactory() {}

    public static ImageIcon create(String category, String name, int size) {
        ImageIcon asset = loadAsset(name, size);
        if (asset != null) return asset;

        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color liquid = switch (category) {
            case "TEA" -> new Color(216, 126, 50);
            case "MATCHA" -> new Color(89, 142, 83);
            case "SMOOTHIE" -> new Color(237, 161, 72);
            default -> new Color(120, 67, 36);
        };
        Color foam = switch (category) {
            case "MATCHA" -> new Color(221, 238, 209);
            case "SMOOTHIE" -> new Color(255, 220, 165);
            default -> new Color(244, 223, 190);
        };

        int cupW = (int) (size * 0.42);
        int cupH = (int) (size * 0.58);
        int x = (size - cupW) / 2;
        int y = (int) (size * 0.18);

        g.setColor(new Color(250, 247, 240));
        g.fillRoundRect(0, 0, size, size, 24, 24);
        g.setColor(new Color(230, 226, 218));
        g.fillOval(x - 4, y + cupH - 8, cupW + 8, 12);

        Polygon cup = new Polygon();
        cup.addPoint(x, y);
        cup.addPoint(x + cupW, y);
        cup.addPoint(x + cupW - 7, y + cupH);
        cup.addPoint(x + 7, y + cupH);
        g.setColor(new Color(255, 255, 255, 190));
        g.fillPolygon(cup);
        g.setColor(new Color(210, 210, 210));
        g.drawPolygon(cup);

        Polygon fill = new Polygon();
        fill.addPoint(x + 5, y + 18);
        fill.addPoint(x + cupW - 5, y + 18);
        fill.addPoint(x + cupW - 10, y + cupH - 6);
        fill.addPoint(x + 10, y + cupH - 6);
        g.setColor(liquid);
        g.fillPolygon(fill);

        g.setColor(foam);
        g.fillOval(x + 6, y + 8, cupW - 12, 18);
        g.setColor(new Color(255, 255, 255, 90));
        g.fillRect(x + cupW / 2 - 3, y + 22, 6, cupH - 34);

        if (name.toLowerCase().contains("latte") || name.toLowerCase().contains("cappuccino")) {
            g.setColor(new Color(255, 255, 255, 150));
            g.drawArc(x + 12, y + 12, cupW - 24, 14, 20, 280);
        }

        g.dispose();
        return new ImageIcon(image);
    }

    private static ImageIcon loadAsset(String name, int size) {
        String path = "/assets/drinks/" + slug(name) + ".png";
        URL url = DrinkIconFactory.class.getResource(path);
        if (url == null) return null;
        ImageIcon sourceIcon = new ImageIcon(url);
        if (sourceIcon.getIconWidth() <= 0 || sourceIcon.getIconHeight() <= 0) return null;

        BufferedImage source = new BufferedImage(
                sourceIcon.getIconWidth(),
                sourceIcon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D sourceGraphics = source.createGraphics();
        sourceGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        sourceGraphics.drawImage(sourceIcon.getImage(), 0, 0, null);
        sourceGraphics.dispose();

        Rectangle bounds = opaqueBounds(source);
        BufferedImage cropped = source.getSubimage(bounds.x, bounds.y, bounds.width, bounds.height);
        int inset = Math.max(4, size / 14);
        int target = Math.max(1, size - inset * 2);
        double scale = Math.min((double) target / cropped.getWidth(), (double) target / cropped.getHeight());
        int drawW = Math.max(1, (int) Math.round(cropped.getWidth() * scale));
        int drawH = Math.max(1, (int) Math.round(cropped.getHeight() * scale));
        int drawX = (size - drawW) / 2;
        int drawY = (size - drawH) / 2;

        BufferedImage canvas = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = canvas.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(cropped, drawX, drawY, drawW, drawH, null);
        g.dispose();
        return new ImageIcon(canvas);
    }

    private static Rectangle opaqueBounds(BufferedImage image) {
        int minX = image.getWidth();
        int minY = image.getHeight();
        int maxX = -1;
        int maxY = -1;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int alpha = (image.getRGB(x, y) >>> 24) & 0xff;
                if (alpha > 8) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }

        if (maxX < minX || maxY < minY) {
            return new Rectangle(0, 0, image.getWidth(), image.getHeight());
        }
        return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }

    private static String slug(String text) {
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return normalized.isBlank() ? "drink" : normalized;
    }
}
