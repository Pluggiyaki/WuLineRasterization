package com.cgvsu.rasterizationfxapp;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;

import com.cgvsu.rasterization.*;
import javafx.scene.paint.Color;

public class RasterizationController {

    @FXML
    AnchorPane anchorPane;
    @FXML
    private Canvas canvas;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // ТЕСТЫ АЛГОРИТМА ВУ:

        // 1. Простая линия (один цвет)
        WuLineAlgorithm.drawWuLine(gc, 50, 50, 200, 100, Color.RED);

        // 2. Линия с градиентом цвета
        WuLineAlgorithm.drawWuLine(gc, 50, 150, 250, 200, Color.RED, Color.BLUE);

        // 3. Вертикальная линия
        WuLineAlgorithm.drawWuLine(gc, 300, 50, 300, 200, Color.GREEN);

        // 4. Горизонтальная линия
        WuLineAlgorithm.drawWuLine(gc, 350, 100, 500, 100, Color.PURPLE);

        // 5. Линия под углом с градиентом
        WuLineAlgorithm.drawWuLine(gc, 400, 50, 550, 180, Color.ORANGE, Color.CYAN);

        // 6. Крутая линия
        WuLineAlgorithm.drawWuLine(gc, 100, 250, 150, 400, Color.BROWN);

        WuLineAlgorithm.drawWuLine(gc, 600, 50, 600, 300, Color.MAGENTA);
        WuLineAlgorithm.drawWuLine(gc, 620, 300, 620, 50, Color.DARKGREEN); // обратное направление

    }

}