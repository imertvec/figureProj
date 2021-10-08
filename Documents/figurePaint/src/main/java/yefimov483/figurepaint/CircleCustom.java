package yefimov483.figurepaint;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class CircleCustom extends Figure{
    private final double radius;
    private double halfSqX;
    private double halfSqY;
    static public int local_id = 0;

    public CircleCustom(double radius, Color color, Canvas canvas){
        this.color = color;
        this.radius = radius;
        this.canvas = canvas;
        this.posX = (canvas.getWidth() / 2);
        this.posY = (canvas.getHeight() / 2);
        local_id++;
        this.name = "cirlcecustom" + local_id;
    }

    public void doDrawing(){
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setStroke(color);
        g.setLineWidth(3);
        g.strokeOval(posX - radius/2, posY - radius/2, radius, radius);
    }

    @Override
    public boolean isSelected(double curX, double curY) {
        halfSqX = ((posX - radius/2) - curX);
        halfSqY = ((posY - radius/2) - curY);
        if(halfSqX * halfSqX + halfSqY * halfSqY <= radius * radius) return true;
        return false;
    }
}
