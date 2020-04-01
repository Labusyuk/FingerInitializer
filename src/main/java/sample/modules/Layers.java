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
    private Dimension layerDimension = new Dimension();

    public Layers(Canvas canvasLayers, Canvas tempCanvas) {
        this.canvasLayers = canvasLayers;
        this.tempCanvas = tempCanvas;
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
        Dimension dimension = new Dimension(0, 0);
        if (snapshot.getWidth() > gc.getCanvas().getWidth() && snapshot.getWidth()>snapshot.getHeight()) {
            double k = snapshot.getWidth() / (gc.getCanvas().getWidth());
            dimension.setSize(snapshot.getWidth() / k, snapshot.getHeight() / k);
        } else {
            double k = snapshot.getHeight() / (gc.getCanvas().getWidth());
            dimension.setSize(snapshot.getWidth() / k, snapshot.getHeight() / k);
        }
        layerDimension = dimension;
        return dimension;
    }

    public void update(){
        GraphicsContext gc = canvasLayers.getGraphicsContext2D();
        canvasLayers.getGraphicsContext2D().clearRect(0, 0, canvasLayers.getWidth(), canvasLayers.getHeight());

        WritableImage snapshot = tempCanvas.snapshot(null, null);
        Dimension canvasLayerDimension = getImageDimenshion(snapshot);

        canvasLayers.setHeight(getCountLayers() * canvasLayerDimension.getHeight());

        for (int i = 0; i < getCountLayers(); i++) {
            gc.drawImage(this.image, (canvasLayers.getWidth()-canvasLayerDimension.getWidth())/2 +1, i*canvasLayerDimension.getHeight(), canvasLayerDimension.getWidth()-2, canvasLayerDimension.getHeight()-2);
            gc.drawImage(getLayer(i).getImage(), (canvasLayers.getWidth()-canvasLayerDimension.getWidth())/2 +1, i*canvasLayerDimension.getHeight(), canvasLayerDimension.getWidth()-2, canvasLayerDimension.getHeight()-2);
            gc.strokeRoundRect((canvasLayers.getWidth()-canvasLayerDimension.getWidth())/2 +1, i*canvasLayerDimension.getHeight(), canvasLayerDimension.getWidth()-2, canvasLayerDimension.getHeight()-2, 10, 10);
        }

    }

    public static List<Layer> getLayers() {
        return layers;
    }

    public static void setLayers(List<Layer> layers) {
        Layers.layers = layers;
    }


}
