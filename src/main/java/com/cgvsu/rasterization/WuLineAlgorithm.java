package com.cgvsu.rasterization;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class WuLineAlgorithm {

    public static void drawWuLine(
            final GraphicsContext graphicsContext,
            final int x1, final int y1,
            final int x2, final int y2,
            final Color color) {

        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();

        // Для начала сделаем простую версию без интерполяции цвета
        // Просто рисуем линию с одним цветом

        boolean steep = Math.abs(y2 - y1) > Math.abs(x2 - x1);

        if (steep) {
            // Меняем местами x и y
            drawWuLine(graphicsContext, y1, x1, y2, x2, color);
            return;
        }

        if (x1 > x2) {
            // Гарантируем, что рисуем слева направо
            drawWuLine(graphicsContext, x2, y2, x1, y1, color);
            return;
        }

        int dx = x2 - x1;
        int dy = y2 - y1;
        float gradient = (dx == 0) ? 1 : (float) dy / dx;

        // Первая конечная точка
        int xend = Math.round(x1);
        float yend = y1 + gradient * (xend - x1);
        float xgap = 1 - (x1 + 0.5f) % 1;
        int xpxl1 = xend;
        int ypxl1 = (int) yend;

        if (steep) {
            plot(pixelWriter, ypxl1, xpxl1, color, 1 - (yend % 1) * xgap);
            plot(pixelWriter, ypxl1 + 1, xpxl1, color, (yend % 1) * xgap);
        } else {
            plot(pixelWriter, xpxl1, ypxl1, color, 1 - (yend % 1) * xgap);
            plot(pixelWriter, xpxl1, ypxl1 + 1, color, (yend % 1) * xgap);
        }

        float intery = yend + gradient;

        // Вторая конечная точка
        xend = Math.round(x2);
        yend = y2 + gradient * (xend - x2);
        xgap = (x2 + 0.5f) % 1;
        int xpxl2 = xend;
        int ypxl2 = (int) yend;

        if (steep) {
            plot(pixelWriter, ypxl2, xpxl2, color, 1 - (yend % 1) * xgap);
            plot(pixelWriter, ypxl2 + 1, xpxl2, color, (yend % 1) * xgap);
        } else {
            plot(pixelWriter, xpxl2, ypxl2, color, 1 - (yend % 1) * xgap);
            plot(pixelWriter, xpxl2, ypxl2 + 1, color, (yend % 1) * xgap);
        }

        // Основной цикл
        for (int x = xpxl1 + 1; x <= xpxl2 - 1; x++) {
            if (steep) {
                plot(pixelWriter, (int) intery, x, color, 1 - (intery % 1));
                plot(pixelWriter, (int) intery + 1, x, color, intery % 1);
            } else {
                plot(pixelWriter, x, (int) intery, color, 1 - (intery % 1));
                plot(pixelWriter, x, (int) intery + 1, color, intery % 1);
            }
            intery += gradient;
        }
    }

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
}