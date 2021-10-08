package yefimov483.figurepaint;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

public class Drawer {
    static public void invalidate(Canvas canvas, List<Figure> list){
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(Color.LIGHTGRAY);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for(Figure x : list){
            x.doDrawing();
        }
    }

}
