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

        boolean steep = Math.abs(y2 - y1) > Math.abs(x2 - x1);

        if (steep) {
            // Меняем x и y местами для крутых линий
            drawWuLineSteep(pixelWriter, x1, y1, x2, y2, color);
            return;
        }

        // Гарантируем рисование слева направо
        if (x1 > x2) {
            drawWuLine(graphicsContext, x2, y2, x1, y1, color);
            return;
        }

        drawWuLineShallow(pixelWriter, x1, y1, x2, y2, color);
    }

    // Метод для крутых линий (почти вертикальных)
    private static void drawWuLineSteep(PixelWriter pixelWriter, int x1, int y1, int x2, int y2, Color color) {
        // Гарантируем рисование сверху вниз
        if (y1 > y2) {
            drawWuLineSteep(pixelWriter, x2, y2, x1, y1, color);
            return;
        }

        int dx = x2 - x1;
        int dy = y2 - y1;
        float gradient = (dy == 0) ? 1 : (float) dx / dy;

        // Первая точка
        int yend = Math.round(y1);
        float xend = x1 + gradient * (yend - y1);
        float ygap = 1 - fractionalPart(y1 + 0.5f);
        int ypxl1 = yend;
        int xpxl1 = (int) xend;

        plot(pixelWriter, xpxl1, ypxl1, color, 1 - fractionalPart(xend) * ygap);
        plot(pixelWriter, xpxl1 + 1, ypxl1, color, fractionalPart(xend) * ygap);

        float interx = xend + gradient;

        // Вторая точка
        yend = Math.round(y2);
        xend = x2 + gradient * (yend - y2);
        ygap = fractionalPart(y2 + 0.5f);
        int ypxl2 = yend;
        int xpxl2 = (int) xend;

        plot(pixelWriter, xpxl2, ypxl2, color, 1 - fractionalPart(xend) * ygap);
        plot(pixelWriter, xpxl2 + 1, ypxl2, color, fractionalPart(xend) * ygap);

        // Основной цикл для крутых линий
        for (int y = ypxl1 + 1; y <= ypxl2 - 1; y++) {
            plot(pixelWriter, (int) interx, y, color, 1 - fractionalPart(interx));
            plot(pixelWriter, (int) interx + 1, y, color, fractionalPart(interx));
            interx += gradient;
        }
    }

    // Метод для пологих линий (почти горизонтальных)
    private static void drawWuLineShallow(PixelWriter pixelWriter, int x1, int y1, int x2, int y2, Color color) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        float gradient = (dx == 0) ? 1 : (float) dy / dx;

        // Первая точка
        int xend = Math.round(x1);
        float yend = y1 + gradient * (xend - x1);
        float xgap = 1 - fractionalPart(x1 + 0.5f);
        int xpxl1 = xend;
        int ypxl1 = (int) yend;

        plot(pixelWriter, xpxl1, ypxl1, color, 1 - fractionalPart(yend) * xgap);
        plot(pixelWriter, xpxl1, ypxl1 + 1, color, fractionalPart(yend) * xgap);

        float intery = yend + gradient;

        // Вторая точка
        xend = Math.round(x2);
        yend = y2 + gradient * (xend - x2);
        xgap = fractionalPart(x2 + 0.5f);
        int xpxl2 = xend;
        int ypxl2 = (int) yend;

        plot(pixelWriter, xpxl2, ypxl2, color, 1 - fractionalPart(yend) * xgap);
        plot(pixelWriter, xpxl2, ypxl2 + 1, color, fractionalPart(yend) * xgap);

        // Основной цикл для пологих линий
        for (int x = xpxl1 + 1; x <= xpxl2 - 1; x++) {
            plot(pixelWriter, x, (int) intery, color, 1 - fractionalPart(intery));
            plot(pixelWriter, x, (int) intery + 1, color, fractionalPart(intery));
            intery += gradient;
        }
    }

    // Метод с интерполяцией цвета
    public static void drawWuLine(
            final GraphicsContext graphicsContext,
            final int x1, final int y1,
            final int x2, final int y2,
            final Color startColor, final Color endColor) {

        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();
        boolean steep = Math.abs(y2 - y1) > Math.abs(x2 - x1);

        if (steep) {
            drawWuLineSteepWithColor(pixelWriter, x1, y1, x2, y2, startColor, endColor);
            return;
        }

        if (x1 > x2) {
            drawWuLine(graphicsContext, x2, y2, x1, y1, endColor, startColor);
            return;
        }

        drawWuLineShallowWithColor(pixelWriter, x1, y1, x2, y2, startColor, endColor);
    }

    // Версия с цветом для крутых линий
    private static void drawWuLineSteepWithColor(PixelWriter pixelWriter, int x1, int y1, int x2, int y2, Color startColor, Color endColor) {
        if (y1 > y2) {
            drawWuLineSteepWithColor(pixelWriter, x2, y2, x1, y1, endColor, startColor);
            return;
        }

        int dx = x2 - x1;
        int dy = y2 - y1;
        float gradient = (dy == 0) ? 1 : (float) dx / dy;

        // Первая точка
        int yend = Math.round(y1);
        float xend = x1 + gradient * (yend - y1);
        float ygap = 1 - fractionalPart(y1 + 0.5f);
        int ypxl1 = yend;
        int xpxl1 = (int) xend;

        // Вторая точка (нужна для интерполяции цвета)
        yend = Math.round(y2);
        xend = x2 + gradient * (yend - y2);
        ygap = fractionalPart(y2 + 0.5f);
        int ypxl2 = yend;
        int xpxl2 = (int) xend;

        // Цвет для первой точки
        Color color1 = interpolateColor(startColor, endColor, 0);
        plot(pixelWriter, xpxl1, ypxl1, color1, 1 - fractionalPart(xend) * ygap);
        plot(pixelWriter, xpxl1 + 1, ypxl1, color1, fractionalPart(xend) * ygap);

        float interx = xend + gradient;

        // Основной цикл с интерполяцией цвета
        for (int y = ypxl1 + 1; y <= ypxl2 - 1; y++) {
            float t = (float) (y - ypxl1) / (ypxl2 - ypxl1);
            Color color = interpolateColor(startColor, endColor, t);

            plot(pixelWriter, (int) interx, y, color, 1 - fractionalPart(interx));
            plot(pixelWriter, (int) interx + 1, y, color, fractionalPart(interx));
            interx += gradient;
        }

        // Цвет для последней точки
        Color color2 = interpolateColor(startColor, endColor, 1);
        plot(pixelWriter, xpxl2, ypxl2, color2, 1 - fractionalPart(xend) * ygap);
        plot(pixelWriter, xpxl2 + 1, ypxl2, color2, fractionalPart(xend) * ygap);
    }

    // Версия с цветом для пологих линий
    private static void drawWuLineShallowWithColor(PixelWriter pixelWriter, int x1, int y1, int x2, int y2, Color startColor, Color endColor) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        float gradient = (dx == 0) ? 1 : (float) dy / dx;

        // Первая точка
        int xend = Math.round(x1);
        float yend = y1 + gradient * (xend - x1);
        float xgap = 1 - fractionalPart(x1 + 0.5f);
        int xpxl1 = xend;
        int ypxl1 = (int) yend;

        // Вторая точка
        xend = Math.round(x2);
        yend = y2 + gradient * (xend - x2);
        xgap = fractionalPart(x2 + 0.5f);
        int xpxl2 = xend;
        int ypxl2 = (int) yend;

        // Цвет для первой точки
        Color color1 = interpolateColor(startColor, endColor, 0);
        plot(pixelWriter, xpxl1, ypxl1, color1, 1 - fractionalPart(yend) * xgap);
        plot(pixelWriter, xpxl1, ypxl1 + 1, color1, fractionalPart(yend) * xgap);

        float intery = yend + gradient;


        for (int x = xpxl1 + 1; x <= xpxl2 - 1; x++) {
            float t = (float) (x - xpxl1) / (xpxl2 - xpxl1);
            Color color = interpolateColor(startColor, endColor, t);

            plot(pixelWriter, x, (int) intery, color, 1 - fractionalPart(intery));
            plot(pixelWriter, x, (int) intery + 1, color, fractionalPart(intery));
            intery += gradient;
        }


        Color color2 = interpolateColor(startColor, endColor, 1);
        plot(pixelWriter, xpxl2, ypxl2, color2, 1 - fractionalPart(yend) * xgap);
        plot(pixelWriter, xpxl2, ypxl2 + 1, color2, fractionalPart(yend) * xgap);
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