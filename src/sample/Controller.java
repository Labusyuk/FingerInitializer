package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import javafx.scene.control.ContextMenu;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Controller {
    private String rootPath, maskPath;
    private static Image image;
    private static int layerCounter = 0;
    private static List<WritableImage> layers = new ArrayList<>();
    private static List<WritableImage> visibleLayers = new ArrayList<>();
    @FXML
    Canvas canvasLayers;
    @FXML
    Slider sliderStroke;
    @FXML
    Canvas canvas;
    @FXML
    Canvas canvasMap;
    Canvas tempCanvas = new Canvas(100,100);
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
                canvasLayers.setHeight(100);
                canvasLayers.getGraphicsContext2D().clearRect(0,0,100,100);
                layerCounter=0;
                tempCanvas.getGraphicsContext2D().clearRect(0,0,tempCanvas.getWidth(),tempCanvas.getHeight());
                layers.clear();
                visibleLayers.clear();
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
        GraphicsContext gcTemp = tempCanvas.getGraphicsContext2D();
        gcTemp.setStroke(Color.BLACK);
        gcTemp.setFill(Color.BLACK);
        gcTemp.fillOval(me.getX()-sizeStroke/2,me.getY()-sizeStroke/2,sizeStroke,sizeStroke);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);
        gc.fillOval(me.getX()-sizeStroke/2,me.getY()-sizeStroke/2,sizeStroke,sizeStroke);
        /*
        WritableImage srcImage = new WritableImage((int) tempCanvas.getWidth(), (int) tempCanvas.getHeight());
        srcImage = tempCanvas.snapshot(null, srcImage);
        PixelReader maskReader = srcImage.getPixelReader();
        int width = (int)tempCanvas.getWidth();
        int height = (int)tempCanvas.getHeight();
        WritableImage dest = new WritableImage(width, height);
        PixelWriter writer = dest.getPixelWriter();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color maskColor = maskReader.getColor(x, y);
                if (!maskColor.equals(Color.WHITE)) {
                    writer.setColor(x, y, maskColor);
                }

            }
        }
        canvas.getGraphicsContext2D().drawImage(dest,0,0);
        */
    }

    public void canvasPaintMouseReleased(MouseEvent me) {
        WritableImage image = canvas.snapshot(null,null);
        redrawCanvasMap();
/*        GraphicsContext gcLayers = canvasLayers.getGraphicsContext2D();
        WritableImage imageLayer = canvas.snapshot(null,null);*/
        int canvasLayersWidth = (int)canvasLayers.getWidth();
        Dimension canvasLayerDimension = new Dimension(0,0);
        if(image.getWidth()>canvasLayersWidth){
            double k = image.getWidth()/(canvasLayersWidth);
            canvasLayerDimension.setSize(image.getWidth()/k, image.getHeight()/k);
        }else
        if(image.getHeight()>canvasLayersWidth){
            double k = image.getHeight()/(canvasLayersWidth);
            canvasLayerDimension.setSize(image.getWidth()/k, image.getHeight()/k);
        }
        WritableImage tempImage = new WritableImage((int) tempCanvas.getWidth(), (int) tempCanvas.getHeight());
        tempImage = tempCanvas.snapshot(null,tempImage);
        PixelReader maskReader = tempImage.getPixelReader();
        int width = (int)tempCanvas.getWidth();
        int height = (int)tempCanvas.getHeight();
        WritableImage dest = new WritableImage(width, height);
        PixelWriter writer = dest.getPixelWriter();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color maskColor = maskReader.getColor(x, y);
                if (maskColor.equals(Color.BLACK)) {
                    writer.setColor(x, y, maskColor);
                }
            }
        }
        layers.add(dest);
        visibleLayers.add(dest);
        layerCounter++;
        tempCanvas.getGraphicsContext2D().clearRect(0,0,tempCanvas.getWidth(),tempCanvas.getHeight());
        redrawCanvasLayers();
    }

    public void drawLayer(ActionEvent ae) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        for(WritableImage writableImage:layers)
            gc.drawImage(writableImage,0,0);
    }

    public void CanvasPaintMousePressed(MouseEvent mouseEvent) {
        tempCanvas.setWidth(canvas.getWidth());
        tempCanvas.setHeight(canvas.getHeight());
        canvasPaint(mouseEvent);

    }

    public void CanvasLayersOnMouseClicked(MouseEvent mouseEvent) {
        int selectedItem = -1;
        ContextMenu contextMenu = new ContextMenu();

        CheckMenuItem showMenuItem = new CheckMenuItem("Показувати");
        showMenuItem.setSelected(false);
        showMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem removeItem = new MenuItem("Видалити слой");

        removeItem.setAccelerator(KeyCombination.keyCombination("Ctrl+D"));


        // Add MenuItem to ContextMenu
        contextMenu.getItems().addAll(showMenuItem, //
                separatorMenuItem, removeItem);

        // When user right-click on Circle
        if (mouseEvent.getButton()==MouseButton.SECONDARY && layerCounter>0 && mouseEvent.getY()>0 && mouseEvent.getY()<(canvasLayers.getWidth()+layerCounter*canvasLayers.getWidth())) {
            selectedItem++;
            Node source = (Node) mouseEvent.getSource();
            Window theStage = source.getScene().getWindow();
            int clickedPosition = (int) mouseEvent.getY();
            while(clickedPosition>canvasLayers.getWidth()) {
                clickedPosition-=canvasLayers.getWidth();
                selectedItem++;
            }
            if(visibleLayers.contains(layers.get(selectedItem)))
            showMenuItem.setSelected(true);
            int finalSelectedItem = selectedItem;
            removeItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    visibleLayers.remove(layers.remove(finalSelectedItem));
                    layerCounter--;
                    redrawCanvasLayers();
                    redrawCanvas();
                    redrawCanvasMap();
                }
            });
            showMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if(visibleLayers.contains(layers.get(finalSelectedItem)))
                        visibleLayers.remove(layers.get(finalSelectedItem));
                    else
                        visibleLayers.add(layers.get(finalSelectedItem));
                    redrawCanvas();
                    redrawCanvasMap();
                }
            });
            System.out.println(selectedItem);
            contextMenu.show(theStage,mouseEvent.getScreenX(),mouseEvent.getScreenY());
        }
    }

    public void redrawCanvasLayers(){
        canvasLayers.getGraphicsContext2D().clearRect(0,0, canvasLayers.getWidth(), canvasLayers.getHeight());
        if(layerCounter>0) {
            int canvasLayersWidth = (int) canvasLayers.getWidth();
            Dimension canvasLayerDimension = new Dimension(0, 0);
            if (image.getWidth() > canvasLayersWidth) {
                double k = image.getWidth() / (canvasLayersWidth);
                canvasLayerDimension.setSize(image.getWidth() / k, image.getHeight() / k);
            } else if (image.getHeight() > canvasLayersWidth) {
                double k = image.getHeight() / (canvasLayersWidth);
                canvasLayerDimension.setSize(image.getWidth() / k, image.getHeight() / k);
            }

            int canvasLayerSize = (int) canvasLayers.getWidth(); // SQUERE
            int imageLayerHeight = (int) canvasLayerDimension.getHeight();
            canvasLayers.setHeight(layerCounter * canvasLayerSize);

            int padding = (canvasLayerSize-imageLayerHeight)/2;
            for(int i=0;i<layerCounter;i++) {
                System.out.println(" :"+padding);
                canvasLayers.getGraphicsContext2D().drawImage(this.image, 0, padding + i * canvasLayerSize, canvasLayerDimension.getWidth(), imageLayerHeight);
                canvasLayers.getGraphicsContext2D().drawImage(layers.get(i), 0, padding + i * canvasLayerSize, canvasLayerDimension.getWidth(), imageLayerHeight);
                canvasLayers.getGraphicsContext2D().strokeRoundRect(0, 0 + i * canvasLayerSize, canvasLayerSize, canvasLayerSize, 10, 10);
            }
        }
    }

    public void redrawCanvas(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        canvas.setHeight(this.image.getHeight());
        canvas.setWidth(this.image.getWidth());
        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        gc.drawImage(this.image, 0, 0,canvas.getWidth(),canvas.getHeight());
        for(int i=0;i<visibleLayers.size();i++){
            gc.drawImage(visibleLayers.get(i),0,0);
        }
    }

    public void redrawCanvasMap(){
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

    }
}
