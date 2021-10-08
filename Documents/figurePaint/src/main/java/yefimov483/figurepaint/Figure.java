package yefimov483.figurepaint;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

abstract public class Figure extends Canvas {
    public Color color;
    public Canvas canvas;
    public double posX;
    public double posY;
    public boolean selected;
    public String name;

    public void doDrawing() {}

    public abstract boolean isSelected(double x, double y);
}
