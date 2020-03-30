package sample.modules;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
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
    private Canvas tempCanvas;

    private Image image;
    WritableImage imageForSelection;
    private Color colorFill = Color.WHITE; ///Color.BLACK
    private Color colorStroke = Color.BLACK; ///Color.WHITE
    private Layers layers;
    private Slider sliderStroke;

    private Dimension workPlaceDimension = new Dimension((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth(),(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight());
    private Dimension minWorkPlaceDimension = new Dimension();
    private Dimension maxWorkPlaceDimension = new Dimension((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()*2,(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()*2);
    private Dimension dimension = new Dimension();
    private ScrollPane paneCanvas;
    private static double koef=100;
    private Rectangle selectionRectangle = new Rectangle();

    public MyCanvas(Canvas canvas, Canvas tempCanvas, ScrollPane paneCanvas, Layers layers, Slider sliderStroke){
        this.canvas = canvas;
        this.layers = layers;
        this.paneCanvas = paneCanvas;
        this.tempCanvas = tempCanvas;
        this.sliderStroke = sliderStroke;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        /*
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3L);
        gc.strokeRoundRect(0, 0, canvas.getWidth(), canvas.getHeight(), 25, 25);
        */
    }

    public void setImage(Image image){
        this.image = image;
       getImageDimenshion();
//        System.out.println("image w="+image.getWidth()+" h="+image.getHeight());
//        System.out.println("dimension w="+dimension.getWidth()+" h="+dimension.getHeight());
        canvas.setWidth(dimension.getWidth());
        canvas.setHeight(dimension.getHeight());
        tempCanvas.setHeight(dimension.getHeight());
        tempCanvas.setWidth(dimension.getWidth());
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, dimension.getWidth(), dimension.getHeight());
        try {
            gc.drawImage(image, 0, 0, dimension.getWidth(), dimension.getHeight());
        }catch (NullPointerException e){
            System.out.println("Помилка завантаження");
            gc.setFont(new Font(20));
            gc.setStroke(Color.WHITE);
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
        /*dimension.setSize(image.getWidth(),image.getHeight());
        System.out.println(dimension);*/
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(this.image, 0, 0, canvas.getWidth(), canvas.getHeight());
        for (Layer layer : layers.getLayers()) {
            if (layer.isVisible())
                gc.drawImage(layer.getImage(), 0, 0,canvas.getWidth(),canvas.getHeight());
        }
    }

    public void resizeUpdate(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        /*dimension.setSize(image.getWidth(),image.getHeight());
        System.out.println(dimension);*/
        getImageDimenshion();
        canvas.setHeight(dimension.getHeight());
        canvas.setWidth(dimension.getWidth());
        tempCanvas.setHeight(dimension.getHeight());
        tempCanvas.setWidth(dimension.getWidth());
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        update();
    }

    public void clearTempCanvas(){
        tempCanvas.getGraphicsContext2D().clearRect(0,0,tempCanvas.getWidth(),tempCanvas.getHeight());
    }

    public void paint(MouseEvent me){
        int sizeStroke = (int) sliderStroke.getValue();
        GraphicsContext gcTemp = tempCanvas.getGraphicsContext2D();
        gcTemp.setStroke(Color.BLACK);
        gcTemp.setFill(Color.BLACK);
        gcTemp.fillOval(me.getX() - sizeStroke / 2, me.getY() - sizeStroke / 2, sizeStroke, sizeStroke);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.WHITE);
        gc.setFill(Color.WHITE);
        gc.fillOval(me.getX() - sizeStroke / 2, me.getY() - sizeStroke / 2, sizeStroke, sizeStroke);
    }

    public void selectionMove(MouseEvent me){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(imageForSelection,selectionRectangle.getX()-2,selectionRectangle.getY()-2,(int)me.getX()-selectionRectangle.getX()+100,(int)me.getY()-selectionRectangle.getY()+100,selectionRectangle.getX()-2,selectionRectangle.getY()-2,(int)me.getX()-selectionRectangle.getX()+100,(int)me.getY()-selectionRectangle.getY()+100);
        gc.setStroke(Color.RED);
        gc.strokeRect(selectionRectangle.getX(),selectionRectangle.getY(),(int)me.getX()-selectionRectangle.getX(),(int)me.getY()-selectionRectangle.getY());
    }

    public void selectionPressed(MouseEvent me){
        update();
        imageForSelection = canvas.snapshot(null, null);
        selectionRectangle.setLocation((int)me.getX(),(int)me.getY());

    }

    public void selectionReleased(MouseEvent me){
        ////ITS A POINT! NOT SIZE!
        selectionRectangle.setSize((int)me.getX(),(int)me.getY());
    }

    public double getKoefSize(){
        return koef;
    }

}
