package sample.modules;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import jdk.nashorn.internal.objects.annotations.Getter;
import lombok.Data;

import java.awt.*;

@Data
public class Map implements EventSubscriber {
    private Canvas canvas, canvasMap;
    private Image image;
    private Color colorFill = Color.BLACK; ///Color.BLACK
    private Color colorStroke = Color.BLACK; ///Color.WHITE

    public Map(Canvas canvasMap, Canvas canvas){
        this.canvasMap = canvasMap;
        this.canvas = canvas;
        GraphicsContext gc = canvasMap.getGraphicsContext2D();
        gc.setFill(colorFill);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        gc.setStroke(colorStroke);
        gc.strokeRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }

    private Dimension getImageDimenshion(Image snapshot){
        GraphicsContext gc = canvasMap.getGraphicsContext2D();
        Dimension dimension = new Dimension(0, 0);
        if (snapshot.getWidth() > gc.getCanvas().getWidth() && snapshot.getWidth()>snapshot.getHeight()) {
            double k = snapshot.getWidth() / (gc.getCanvas().getWidth());
            dimension.setSize(snapshot.getWidth() / k, snapshot.getHeight() / k);
        } else {
            double k = snapshot.getHeight() / (gc.getCanvas().getHeight());
            dimension.setSize(snapshot.getWidth() / k, snapshot.getHeight() / k);
        }
        return dimension;
    }
    public void update(){
        GraphicsContext gc = canvasMap.getGraphicsContext2D();
        WritableImage snapshot = canvas.snapshot(null, null);
        Dimension imageDimension = getImageDimenshion(snapshot);
        gc.setFill(colorFill);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        gc.setStroke(colorStroke);
        gc.strokeRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        gc.drawImage(snapshot, (gc.getCanvas().getWidth() - imageDimension.getWidth()) / 2 + 1, (gc.getCanvas().getHeight() - imageDimension.getHeight()) / 2 + 1, imageDimension.getWidth() - 2, imageDimension.getHeight() - 2);
    }
}
