package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Controller {
    private String rootPath, maskPath;
    private static Image image;
    @FXML
    Canvas canvasLayers;
    @FXML
    Slider sliderStroke;
    @FXML
    Canvas canvas;
    @FXML
    Canvas canvasMap;
    Canvas tempCanvas;
    @FXML
    Text textRootPath;
    @FXML
    Text textMaskPath;
    @FXML
    ListView<String> listViewFileList;
    @FXML
    public void initialize(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3L);
        gc.strokeRoundRect(0,0,gc.getCanvas().getWidth(),gc.getCanvas().getHeight(),25,25);

        MultipleSelectionModel<String> langsSelectionModel = listViewFileList.getSelectionModel();
        langsSelectionModel.setSelectionMode(SelectionMode.MULTIPLE);
        sliderStroke.setMax(200);
        sliderStroke.setValue(50);



        // устанавливаем слушатель для отслеживания изменений
        langsSelectionModel.selectedItemProperty().addListener(new ChangeListener<String>(){
            public void changed(ObservableValue<? extends String> changed, String oldValue, String newValue){
                String selectedItems = "";
                ObservableList<String> selected = langsSelectionModel.getSelectedItems();
                for (String item : selected){
                    selectedItems += item + " ";
                }
                image = new Image("file:///" + rootPath + "\\" + selectedItems);
                GraphicsContext gcCanvasMap = canvasMap.getGraphicsContext2D();
                gcCanvasMap.setFill(Color.BLACK);
                gcCanvasMap.fillRect(0,0,gcCanvasMap.getCanvas().getWidth(),gcCanvasMap.getCanvas().getHeight());
                Dimension canvasMapDimension = new Dimension(0,0);
                if(image.getWidth()>gcCanvasMap.getCanvas().getWidth()){
                    double k = image.getWidth()/(gcCanvasMap.getCanvas().getWidth());
                    canvasMapDimension.setSize(image.getWidth()/k, image.getHeight()/k);
                }else
                if(image.getHeight()>gcCanvasMap.getCanvas().getHeight()){
                    double k = image.getHeight()/(gcCanvasMap.getCanvas().getHeight());
                    canvasMapDimension.setSize(image.getWidth()/k, image.getHeight()/k);
                }
                gcCanvasMap.drawImage(image, (gcCanvasMap.getCanvas().getWidth() - canvasMapDimension.getWidth())/2+1, (gcCanvasMap.getCanvas().getHeight() - canvasMapDimension.getHeight())/2+1, canvasMapDimension.getWidth()-2, canvasMapDimension.getHeight()-2);

                GraphicsContext gc = canvas.getGraphicsContext2D();

                canvas.setWidth(image.getWidth());
                canvas.setHeight(image.getHeight());
                gc.setFill(Color.BLACK);
                gc.fillRect(0, 0, image.getWidth(), image.getHeight());
                gc.drawImage(image, 0, 0, image.getWidth(), image.getHeight());
            }
        });
    }

    public void chooseRoot(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Window theStage = source.getScene().getWindow();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File directory = directoryChooser.showDialog(theStage);
        if (directory != null){
            List<String> fileList = new ArrayList<>();
            fileList.sort(Comparator.naturalOrder());
            rootPath = directory.toString();
            textRootPath.setText("Корінний шлях:\t " + rootPath);
            File[] files = directory.listFiles();
            for(File file: files)
                if (file.isFile() && Main.format.contains(getExtensionByStringHandling(file.getName()))) fileList.add(file.getName());
                listViewFileList.setEditable(false);
                listViewFileList.setItems(FXCollections.observableList(fileList));
        }
    }

    public String getExtensionByStringHandling(String filename) {
        int index = filename.indexOf('.');
        String extension = index == -1? null : filename.substring(index);
        return extension;
    }

    public void canvasPaint(MouseEvent me) {
        int sizeStroke = (int) sliderStroke.getValue();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);
        gc.fillOval(me.getX()-sizeStroke/2,me.getY()-sizeStroke/2,sizeStroke,sizeStroke);
    }

    public void canvasPaintMouseReleased(MouseEvent me) {
        WritableImage image = canvas.snapshot(null,null);
        GraphicsContext gc = canvasMap.getGraphicsContext2D();
        Dimension canvasMapDimension = new Dimension(0,0);
        if(image.getWidth()>gc.getCanvas().getWidth()){
            double k = image.getWidth()/(gc.getCanvas().getWidth());
            canvasMapDimension.setSize(image.getWidth()/k, image.getHeight()/k);
        }else
        if(image.getHeight()>gc.getCanvas().getHeight()){
            double k = image.getHeight()/(gc.getCanvas().getHeight());
            canvasMapDimension.setSize(image.getWidth()/k, image.getHeight()/k);
        }
        gc.drawImage(image, (gc.getCanvas().getWidth() - canvasMapDimension.getWidth())/2+1, (gc.getCanvas().getHeight() - canvasMapDimension.getHeight())/2+1, canvasMapDimension.getWidth()-2, canvasMapDimension.getHeight()-2);
        GraphicsContext gcLayers = canvasLayers.getGraphicsContext2D();

        WritableImage imageLayer = canvas.snapshot(null,null);
    }

    public void drawLayer(ActionEvent ae) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0,0,100,100);
        if(image!=null)
        gc.drawImage(image, 0, 0, image.getWidth(), image.getHeight());



    }
}
