package com.cgvsu.rasterization;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class WuLineAlgorithm {

    // Простая версия с одним цветом
    public static void drawWuLine(
            final GraphicsContext graphicsContext,
            final int x1, final int y1,
            final int x2, final int y2,
            final Color color) {

        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();

        boolean steep = Math.abs(y2 - y1) > Math.abs(x2 - x1);

        if (steep) {
            drawWuLine(graphicsContext, y1, x1, y2, x2, color);
            return;
        }

        if (x1 > x2) {
            drawWuLine(graphicsContext, x2, y2, x1, y1, color);
            return;
        }

        int dx = x2 - x1;
        int dy = y2 - y1;
        float gradient = (dx == 0) ? 1 : (float) dy / dx;

        // Первая конечная точка
        int xend = Math.round(x1);
        float yend = y1 + gradient * (xend - x1);
        float xgap = 1 - fractionalPart(x1 + 0.5f);
        int xpxl1 = xend;
        int ypxl1 = (int) yend;

        if (steep) {
            plot(pixelWriter, ypxl1, xpxl1, color, 1 - fractionalPart(yend) * xgap);
            plot(pixelWriter, ypxl1 + 1, xpxl1, color, fractionalPart(yend) * xgap);
        } else {
            plot(pixelWriter, xpxl1, ypxl1, color, 1 - fractionalPart(yend) * xgap);
            plot(pixelWriter, xpxl1, ypxl1 + 1, color, fractionalPart(yend) * xgap);
        }

        float intery = yend + gradient;

        // Вторая конечная точка
        xend = Math.round(x2);
        yend = y2 + gradient * (xend - x2);
        xgap = fractionalPart(x2 + 0.5f);
        int xpxl2 = xend;
        int ypxl2 = (int) yend;

        if (steep) {
            plot(pixelWriter, ypxl2, xpxl2, color, 1 - fractionalPart(yend) * xgap);
            plot(pixelWriter, ypxl2 + 1, xpxl2, color, fractionalPart(yend) * xgap);
        } else {
            plot(pixelWriter, xpxl2, ypxl2, color, 1 - fractionalPart(yend) * xgap);
            plot(pixelWriter, xpxl2, ypxl2 + 1, color, fractionalPart(yend) * xgap);
        }

        // Основной цикл
        for (int x = xpxl1 + 1; x <= xpxl2 - 1; x++) {
            if (steep) {
                plot(pixelWriter, (int) intery, x, color, 1 - fractionalPart(intery));
                plot(pixelWriter, (int) intery + 1, x, color, fractionalPart(intery));
            } else {
                plot(pixelWriter, x, (int) intery, color, 1 - fractionalPart(intery));
                plot(pixelWriter, x, (int) intery + 1, color, fractionalPart(intery));
            }
            intery += gradient;
        }
    }

    // Версия с интерполяцией цвета
    public static void drawWuLine(
            final GraphicsContext graphicsContext,
            final int x1, final int y1,
            final int x2, final int y2,
            final Color startColor, final Color endColor) {

        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();
        boolean steep = Math.abs(y2 - y1) > Math.abs(x2 - x1);

        if (steep) {
            drawWuLine(graphicsContext, y1, x1, y2, x2, startColor, endColor);
            return;
        }

        if (x1 > x2) {
            drawWuLine(graphicsContext, x2, y2, x1, y1, endColor, startColor);
            return;
        }

        int dx = x2 - x1;
        int dy = y2 - y1;
        float gradient = (dx == 0) ? 1 : (float) dy / dx;

        // Первая точка
        int xend = Math.round(x1);
        float yend = y1 + gradient * (xend - x1);
        float xgap = 1 - fractionalPart(x1 + 0.5f);
        int xpxl1 = xend;
        int ypxl1 = (int) yend;

        // Вторая точка (объявляем ЗДЕСЬ!)
        xend = Math.round(x2);
        yend = y2 + gradient * (xend - x2);
        xgap = fractionalPart(x2 + 0.5f);
        int xpxl2 = xend;
        int ypxl2 = (int) yend;

        // Цвет для первой точки
        Color color1 = interpolateColor(startColor, endColor, 0);

        if (steep) {
            plot(pixelWriter, ypxl1, xpxl1, color1, 1 - fractionalPart(yend) * xgap);
            plot(pixelWriter, ypxl1 + 1, xpxl1, color1, fractionalPart(yend) * xgap);
        } else {
            plot(pixelWriter, xpxl1, ypxl1, color1, 1 - fractionalPart(yend) * xgap);
            plot(pixelWriter, xpxl1, ypxl1 + 1, color1, fractionalPart(yend) * xgap);
        }

        float intery = yend + gradient;

        // Основной цикл с интерполяцией цвета
        for (int x = xpxl1 + 1; x <= xpxl2 - 1; x++) {
            // Интерполяция цвета на основе позиции
            float t = (float) (x - xpxl1) / (xpxl2 - xpxl1);
            Color color = interpolateColor(startColor, endColor, t);

            if (steep) {
                plot(pixelWriter, (int) intery, x, color, 1 - fractionalPart(intery));
                plot(pixelWriter, (int) intery + 1, x, color, fractionalPart(intery));
            } else {
                plot(pixelWriter, x, (int) intery, color, 1 - fractionalPart(intery));
                plot(pixelWriter, x, (int) intery + 1, color, fractionalPart(intery));
            }
            intery += gradient;
        }

        // Цвет для последней точки
        Color color2 = interpolateColor(startColor, endColor, 1);

        if (steep) {
            plot(pixelWriter, ypxl2, xpxl2, color2, 1 - fractionalPart(yend) * xgap);
            plot(pixelWriter, ypxl2 + 1, xpxl2, color2, fractionalPart(yend) * xgap);
        } else {
            plot(pixelWriter, xpxl2, ypxl2, color2, 1 - fractionalPart(yend) * xgap);
            plot(pixelWriter, xpxl2, ypxl2 + 1, color2, fractionalPart(yend) * xgap);
        }
    }

    // Вспомогательные методы
    private static void plot(PixelWriter pixelWriter, int x, int y, Color color, float intensity) {
        if (intensity <= 0) return;

        Color finalColor = new Color(
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                color.getOpacity() * intensity
        );

        if (x >= 0 && y >= 0) {
            pixelWriter.setColor(x, y, finalColor);
        }
    }

    private static float fractionalPart(float x) {
        return x - (int) x;
    }

    private static Color interpolateColor(Color start, Color end, float t) {
        double r = start.getRed() + (end.getRed() - start.getRed()) * t;
        double g = start.getGreen() + (end.getGreen() - start.getGreen()) * t;
        double b = start.getBlue() + (end.getBlue() - start.getBlue()) * t;
        double a = start.getOpacity() + (end.getOpacity() - start.getOpacity()) * t;

        return new Color(r, g, b, a);
    }
}