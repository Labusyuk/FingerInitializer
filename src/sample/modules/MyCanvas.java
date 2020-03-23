package sample.modules;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import sample.entity.Layer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class MyCanvas implements EventSubscriber{
    private Canvas canvas;
    private Image image;
    private Color colorFill = Color.WHITE; ///Color.BLACK
    private Color colorStroke = Color.BLACK; ///Color.WHITE
    private static List<Layer> layers = new ArrayList<>();
    private Dimension workingDimension = new Dimension(1000,1000);

    private Dimension getImageDimenshion(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Dimension dimension = new Dimension(0, 0);
        if (image.getWidth() > workingDimension.getWidth() && image.getWidth()>image.getHeight()) {
            double k = image.getWidth() / (workingDimension.getWidth());
            dimension.setSize(image.getWidth() / k, image.getHeight() / k);
        } else {
            double k = image.getHeight() / (workingDimension.getHeight());
            dimension.setSize(image.getWidth() / k, image.getHeight() / k);
        }
        return dimension;
    }
    public void update(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Dimension imageDimension = getImageDimenshion();
        gc.setFill(colorFill);
        gc.setStroke(colorStroke);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        gc.strokeRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        gc.drawImage(image, (gc.getCanvas().getWidth() - imageDimension.getWidth()) / 2 + 1, (gc.getCanvas().getHeight() - imageDimension.getHeight()) / 2 + 1, imageDimension.getWidth() - 2, imageDimension.getHeight() - 2);
    }
}
