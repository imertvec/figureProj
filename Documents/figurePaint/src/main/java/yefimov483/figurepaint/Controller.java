package yefimov483.figurepaint;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Controller {
    private String[] lst = new String[]{"CircleCustom", "SquareCustom"};
    private Figure fig;
    private List<Figure> figs = new ArrayList<>();
    private String selectedName;

    @FXML
    private Canvas canvas;

    @FXML
    public ComboBox<String> comboBox;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private ListView list;

    @FXML
    protected void addAction() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        switch (comboBox.getValue().toLowerCase()){
            case "circlecustom": fig = new CircleCustom(50, colorPicker.getValue(), canvas);
                break;
            case "squarecustom": fig = new SquareCustom(50, colorPicker.getValue(), canvas);
                break;
        }

        list.getItems().add(fig.name);
        figs.add(fig);
        Drawer.invalidate(canvas, figs);
    }

    @FXML
    protected void deleteAction(){
        for(int i = 0; i < list.getItems().size(); i++){
            if(list.getItems().get(i) == selectedName) {
                list.getItems().remove(i);
                figs.remove(i);
                Drawer.invalidate(canvas, figs);
                break;
            }
        }
    }

    public void initialize() {
        //default setting for combo
        comboBox.setItems(FXCollections.observableList(Arrays.stream(lst).toList()));
        comboBox.getSelectionModel().selectFirst();

        //fill canvas with starting
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(Color.LIGHTGRAY);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        //figure clicked
        canvas.setOnMouseClicked(event -> {
            for(Figure x : figs){
                if(x.isSelected(event.getX(), event.getY())){
                    x.selected = true;
                    selectedName = x.name;
                    for(int i = 0; i < list.getItems().size(); i++){
                        if(list.getItems().get(i) == selectedName){
                            list.getSelectionModel().select(i);
                            break;
                        }
                    }
                    break;
                }
                else x.selected = false;
            }
            Drawer.invalidate(canvas, figs);
        });

        //figure mouseMoved
        canvas.setOnMouseDragged(event ->{
            for(Figure x : figs){
                if(x.isSelected(event.getX(), event.getY()) && x.selected){
                    x.posX = event.getX();
                    x.posY = event.getY();
                    Drawer.invalidate(canvas, figs);


                }
            }
        });

        //list clicked
        list.setOnMouseClicked(event -> {
            for(Figure x : figs){
                if(x.name.equals(list.getSelectionModel().getSelectedItem())){
                    selectedName = x.name;
                    x.selected = true;
                    break;
                }
            }
        });
    }

}