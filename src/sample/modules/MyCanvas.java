package sample.modules;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import lombok.Data;
import sample.entity.Layer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class MyCanvas implements EventSubscriber{
    private Canvas canvas;
    private Image image;
    private Color colorFill = Color.WHITE; ///Color.BLACK
    private Color colorStroke = Color.BLACK; ///Color.WHITE
    private Layers layers;
    private Dimension workPlaceDimension = new Dimension(1000,1000);
    private Dimension dimension = new Dimension();
    private ScrollPane paneCanvas;
    private static double koef=100;

    public MyCanvas(Canvas canvas, ScrollPane paneCanvas, Layers layers){
        this.canvas = canvas;
        this.layers = layers;
        this.paneCanvas = paneCanvas;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        /*
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3L);
        gc.strokeRoundRect(0, 0, canvas.getWidth(), canvas.getHeight(), 25, 25);
        */
    }

    public void setImage(Image image){
        this.image = image;
        workPlaceDimension.setSize(paneCanvas.getWidth(),paneCanvas.getHeight());
        getImageDimenshion();
//        System.out.println("image w="+image.getWidth()+" h="+image.getHeight());
//        System.out.println("dimension w="+dimension.getWidth()+" h="+dimension.getHeight());
        canvas.setWidth(dimension.getWidth());
        canvas.setHeight(dimension.getHeight());
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, dimension.getWidth(), dimension.getHeight());
        try {
            gc.drawImage(image, 0, 0, dimension.getWidth(), dimension.getHeight());
        }catch (NullPointerException e){
            gc.setFont(new Font(20));
            gc.strokeText("Помилка завантаження", 100,100);
        }
    }

    private void getImageDimenshion(){
        if(image.getWidth() > workPlaceDimension.getWidth() && image.getWidth()>image.getHeight()) {
            koef = image.getWidth() / (workPlaceDimension.getWidth());
        } else {
            koef = image.getHeight() / (workPlaceDimension.getHeight());
        }
        if(image.getWidth() < workPlaceDimension.getWidth() && image.getHeight() < workPlaceDimension.getHeight())
            dimension.setSize(image.getWidth(),image.getHeight());
        else
        dimension.setSize(image.getWidth() / (float)koef, image.getHeight() / (float)koef);
    }

    public void update() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        canvas.setHeight(dimension.getHeight());
        canvas.setWidth(dimension.getWidth());
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(this.image, 0, 0, canvas.getWidth(), canvas.getHeight());
        for (Layer layer : layers.getLayers()) {
            if (layer.isVisible())
                gc.drawImage(layer.getImage(), 0, 0);
        }
    }

    public double getKoefSize(){
        return koef;
    }

}
