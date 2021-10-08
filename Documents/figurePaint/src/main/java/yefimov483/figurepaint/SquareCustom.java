package yefimov483.figurepaint;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SquareCustom extends Figure{
    private final double side;
    private double width;
    private double height;
    static public int local_id = 0;

    public SquareCustom(double side, Color color, Canvas canvas){
        this.color = color;
        this.side = side;
        this.canvas = canvas;
        this.posX = (canvas.getWidth() / 2);
        this.posY = (canvas.getHeight() / 2);
        local_id++;
        this.name = "squarecustom" + local_id;
    }

    public void doDrawing(){
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setStroke(color);
        g.setLineWidth(3);
        g.strokeRect(posX - side/2, posY - side/2, side, side);
    }

    public boolean isSelected(double curX, double curY){
        if(curX >= posX - side/2 && curX <= posX + side/2 && curY >= posY - side/2 && curY <= posY + side/2) return true;
        return false;
    }
}
