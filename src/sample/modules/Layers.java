package sample.modules;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Data;
import sample.entity.Layer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

@Data
public class Layers{

    private static List<Layer> layers = new ArrayList<>();
    private Canvas canvasLayers, tempCanvas;
    private Image image;

    public Layers(Canvas canvasLayers, Canvas tempCanvas, Image image) {
        this.canvasLayers = canvasLayers;
        this.tempCanvas = tempCanvas;
        this.image = image;
        canvasLayers.setHeight(100);
        canvasLayers.getGraphicsContext2D().clearRect(0, 0, 100, 100);
    }

    public int getCountLayers(){
        return layers.size();
    }

    public int getCountVisibleLayers(){
        int countVisibleLayers = 0;
        for(Layer layer:layers){
            if(layer.isVisible())
                countVisibleLayers++;
        }
        return countVisibleLayers;
    }

    public void addLayer(Layer layer){
        layers.add(layer);
    }

    public Layer getLayer(int index){
        return layers.get(index);
    }


    public boolean isVisible(int index){
        return layers.get(index).isVisible();
    }

    public void removeLayer(int index){
        layers.remove(index);
    }

    public void clear(){
        layers.clear();
        canvasLayers.getGraphicsContext2D().clearRect(0, 0, canvasLayers.getWidth(), canvasLayers.getHeight());
    }

    public boolean isEmpty(){
        return layers.isEmpty();
    }

    private Dimension getImageDimenshion(Image snapshot){
        GraphicsContext gc = canvasLayers.getGraphicsContext2D();
        int canvasLayersWidth = (int) canvasLayers.getWidth();
        Dimension canvasLayerDimension = new Dimension(0, 0);
        double k;
        if (image.getWidth() > canvasLayersWidth && image.getWidth()>image.getHeight()) {
            k = image.getWidth() / (canvasLayersWidth);
            canvasLayerDimension.setSize(image.getWidth() / k, image.getHeight() / k);
        }else{
            k = image.getHeight() / (canvasLayersWidth);
            canvasLayerDimension.setSize(image.getWidth() / k, image.getHeight() / k);
        }
        return canvasLayerDimension;
    }
    public void update(){
        GraphicsContext gc = canvasLayers.getGraphicsContext2D();
        canvasLayers.getGraphicsContext2D().clearRect(0, 0, canvasLayers.getWidth(), canvasLayers.getHeight());

        WritableImage snapshot = tempCanvas.snapshot(null, null);
        Dimension canvasLayerDimension = getImageDimenshion(snapshot);

        int canvasLayerSize = (int) canvasLayers.getWidth(); // SQUERE
        int imageLayerHeight = (int) canvasLayerDimension.getHeight();
        canvasLayers.setHeight(getCountLayers() * canvasLayerSize);

        int padding = (canvasLayerSize - imageLayerHeight) / 2;
        int paddingX = (int)(canvasLayerDimension.getWidth()<canvasLayerSize?(canvasLayerSize-canvasLayerDimension.getWidth())/2:0);
        for (int i = 0; i < getCountLayers(); i++) {
            gc.drawImage(this.image, paddingX, padding + i * canvasLayerSize, canvasLayerDimension.getWidth(), imageLayerHeight);
            gc.drawImage(getLayer(i).getImage(), paddingX, padding + i * canvasLayerSize, canvasLayerDimension.getWidth(), imageLayerHeight);
            gc.strokeRoundRect(0, 0 + i * canvasLayerSize, canvasLayerSize, canvasLayerSize, 10, 10);
        }

    }

    public static List<Layer> getLayers() {
        return layers;
    }

    public static void setLayers(List<Layer> layers) {
        Layers.layers = layers;
    }
}
