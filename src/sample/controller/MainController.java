package sample.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.control.ContextMenu;
import sample.Main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class MainController {
    private String rootPath, maskPath;
    private static Image image;
    private static int layerCounter = 0;
    private static List<WritableImage> layers = new ArrayList<>();
    private static List<WritableImage> visibleLayers = new ArrayList<>();
    @FXML
    MenuItem menuBarChangeRootPath;
    @FXML
    MenuItem menuBarChangeMaskPath;
    @FXML
    MenuItem menuBarChangeHelp;
    @FXML
    Canvas canvasLayers;
    @FXML
    Slider sliderStroke;
    @FXML
    ScrollPane paneCanvas;
    @FXML
    Canvas canvas;
    @FXML
    Canvas canvasMap;
    Canvas tempCanvas = new Canvas(100, 100);
    @FXML
    Text textRootPath;
    @FXML
    Text textMaskPath;
    @FXML
    ListView<String> listViewFileList;
    @FXML
    Button saveAndNext;
    @FXML
    Button saveFragment;
    @FXML
    Button save;
    @FXML
    ImageView cyberSecurityImg;
    @FXML
    ImageView cyberPoliceImg;

    @FXML
    public void initialize() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3L);
        gc.strokeRoundRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight(), 25, 25);

        MultipleSelectionModel<String> langsSelectionModel = listViewFileList.getSelectionModel();
        langsSelectionModel.setSelectionMode(SelectionMode.MULTIPLE);
        sliderStroke.setMax(200);
        sliderStroke.setValue(50);
        listViewFileList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);


        // устанавливаем слушатель для отслеживания изменений
        langsSelectionModel.selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> changed, String oldValue, String newValue) {
                String selectedItems = "";
                ObservableList<String> selected = langsSelectionModel.getSelectedItems();
                for (String item : selected) {
                    selectedItems += item + " ";
                }
                image = new Image("file:///" + rootPath + "\\" + selectedItems);
                GraphicsContext gcCanvasMap = canvasMap.getGraphicsContext2D();
                gcCanvasMap.setFill(Color.BLACK);
                gcCanvasMap.fillRect(0, 0, gcCanvasMap.getCanvas().getWidth(), gcCanvasMap.getCanvas().getHeight());
                Dimension canvasMapDimension = new Dimension(0, 0);
                if (image.getWidth() > gcCanvasMap.getCanvas().getWidth()) {
                    double k = image.getWidth() / (gcCanvasMap.getCanvas().getWidth());
                    canvasMapDimension.setSize(image.getWidth() / k, image.getHeight() / k);
                } else if (image.getHeight() > gcCanvasMap.getCanvas().getHeight()) {
                    double k = image.getHeight() / (gcCanvasMap.getCanvas().getHeight());
                    canvasMapDimension.setSize(image.getWidth() / k, image.getHeight() / k);
                }
                gcCanvasMap.drawImage(image, (gcCanvasMap.getCanvas().getWidth() - canvasMapDimension.getWidth()) / 2 + 1, (gcCanvasMap.getCanvas().getHeight() - canvasMapDimension.getHeight()) / 2 + 1, canvasMapDimension.getWidth() - 2, canvasMapDimension.getHeight() - 2);

                GraphicsContext gc = canvas.getGraphicsContext2D();

                canvas.setWidth(image.getWidth());
                canvas.setHeight(image.getHeight());
                gc.setFill(Color.BLACK);
                gc.fillRect(0, 0, image.getWidth(), image.getHeight());
                gc.drawImage(image, 0, 0, image.getWidth(), image.getHeight());
                canvasLayers.setHeight(100);
                canvasLayers.getGraphicsContext2D().clearRect(0, 0, 100, 100);
                layerCounter = 0;
                tempCanvas.getGraphicsContext2D().clearRect(0, 0, tempCanvas.getWidth(), tempCanvas.getHeight());
                layers.clear();
                visibleLayers.clear();
                activateDeactivateControlls(false);
            }
        });
        menuBarChangeRootPath.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                chooseRoot(event);
            }
        });
        menuBarChangeMaskPath.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                chooseMaskRoot(event);
            }
        });
        menuBarChangeHelp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Parent root = null;
                try {
                    root = FXMLLoader.load(getClass().getResource("/fxml/help.fxml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Scene secondScene = new Scene(root, 600, 400);

                // New window (Stage)
                Stage newWindow = new Stage();
                newWindow.setTitle("Про програму");
                newWindow.setScene(secondScene);

                // Set position of second window, related to primary window.

                newWindow.show();
            }
        });
    }

    public void chooseRoot(ActionEvent actionEvent) {
        Window theStage = null;
        if(actionEvent.getSource() instanceof Node) {
            Node source = (Node) actionEvent.getSource();
            theStage = source.getScene().getWindow();
        }else if(actionEvent.getSource() instanceof MenuItem){
            ContextMenu contextMenu = ((MenuItem)(actionEvent.getSource())).getParentPopup();
            theStage = contextMenu.getScene().getWindow();
        }
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File directory = directoryChooser.showDialog(theStage);
        if (directory != null) {
            List<String> fileList = new ArrayList<>();
            fileList.sort(Comparator.naturalOrder());
            rootPath = directory.toString();
            textRootPath.setText("Корінний шлях:\t " + rootPath);
            File[] files = directory.listFiles();
            for (File file : files)
                if (file.isFile() && Main.format.contains(getExtensionByStringHandling(file.getName())))
                    fileList.add(file.getName());
            listViewFileList.setEditable(false);
            listViewFileList.setItems(FXCollections.observableList(fileList));
            if (0 < listViewFileList.getItems().size()) {
                listViewFileList.getSelectionModel().select(0);
                canvas.setDisable(false);
            }
        }
    }

    public void chooseMaskRoot(ActionEvent actionEvent) {
        Window theStage = null;
        if(actionEvent.getSource() instanceof Node) {
            Node source = (Node) actionEvent.getSource();
            theStage = source.getScene().getWindow();
        }else if(actionEvent.getSource() instanceof MenuItem){
            ContextMenu contextMenu = ((MenuItem)(actionEvent.getSource())).getParentPopup();
            theStage = contextMenu.getScene().getWindow();
        }
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File directory = directoryChooser.showDialog(theStage);
        if (directory != null) {
            maskPath = directory.toString();
            textMaskPath.setText("Вихідний шлях:\t " + maskPath);
        }
    }

    public String getExtensionByStringHandling(String filename) {
        int index = filename.indexOf('.');
        String extension = index == -1 ? null : filename.substring(index);
        return extension;
    }

    public String getNameWithoutExtension(String filename) {
        int index = filename.indexOf('.');
        String extension = index == -1 ? null : filename.substring(0, index);
        return extension;
    }

    public static boolean isLong(String s) {
        try {
            Long.parseLong(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    public void canvasPaint(MouseEvent me) {
        int sizeStroke = (int) sliderStroke.getValue();
        GraphicsContext gcTemp = tempCanvas.getGraphicsContext2D();
        gcTemp.setStroke(Color.BLACK);
        gcTemp.setFill(Color.BLACK);
        gcTemp.fillOval(me.getX() - sizeStroke / 2, me.getY() - sizeStroke / 2, sizeStroke, sizeStroke);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.WHITE);
        gc.setFill(Color.WHITE);
        gc.fillOval(me.getX() - sizeStroke / 2, me.getY() - sizeStroke / 2, sizeStroke, sizeStroke);
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
        WritableImage image = canvas.snapshot(null, null);
        redrawCanvasMap();
/*        GraphicsContext gcLayers = canvasLayers.getGraphicsContext2D();
        WritableImage imageLayer = canvas.snapshot(null,null);*/
/*        int canvasLayersWidth = (int) canvasLayers.getWidth();
        Dimension canvasLayerDimension = new Dimension(0, 0);
        if (image.getWidth() > canvasLayersWidth) {
            double k = image.getWidth() / (canvasLayersWidth);
            canvasLayerDimension.setSize(image.getWidth() / k, image.getHeight() / k);
        } else if (image.getHeight() > canvasLayersWidth) {
            double k = image.getHeight() / (canvasLayersWidth);
            canvasLayerDimension.setSize(image.getWidth() / k, image.getHeight() / k);
        }*/
        WritableImage tempImage = new WritableImage((int) tempCanvas.getWidth(), (int) tempCanvas.getHeight());
        tempImage = tempCanvas.snapshot(null, tempImage);
        PixelReader maskReader = tempImage.getPixelReader();
        int width = (int) tempCanvas.getWidth();
        int height = (int) tempCanvas.getHeight();
        WritableImage dest = new WritableImage(width, height);
        PixelWriter writer = dest.getPixelWriter();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color maskColor = maskReader.getColor(x, y);
                if (maskColor.equals(Color.BLACK)) {
                    writer.setColor(x, y, Color.WHITE);
                }
            }
        }
        layers.add(dest);
        visibleLayers.add(dest);
        layerCounter++;
        tempCanvas.getGraphicsContext2D().clearRect(0, 0, tempCanvas.getWidth(), tempCanvas.getHeight());
        redrawCanvasLayers();
        activateDeactivateControlls(true);
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
        if (mouseEvent.getButton() == MouseButton.SECONDARY && layerCounter > 0 && mouseEvent.getY() > 0 && mouseEvent.getY() < (canvasLayers.getWidth() + layerCounter * canvasLayers.getWidth())) {
            selectedItem++;
            Node source = (Node) mouseEvent.getSource();
            Window theStage = source.getScene().getWindow();
            int clickedPosition = (int) mouseEvent.getY();
            while (clickedPosition > canvasLayers.getWidth()) {
                clickedPosition -= canvasLayers.getWidth();
                selectedItem++;
            }
            if (visibleLayers.contains(layers.get(selectedItem)))
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
                    if(layers.isEmpty())activateDeactivateControlls(false);
                }
            });
            showMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (visibleLayers.contains(layers.get(finalSelectedItem)))
                        visibleLayers.remove(layers.get(finalSelectedItem));
                    else
                        visibleLayers.add(layers.get(finalSelectedItem));
                    redrawCanvas();
                    redrawCanvasMap();
                }
            });
            contextMenu.show(theStage, mouseEvent.getScreenX(), mouseEvent.getScreenY());
        }
    }

    public void redrawCanvasLayers() {
        canvasLayers.getGraphicsContext2D().clearRect(0, 0, canvasLayers.getWidth(), canvasLayers.getHeight());
        if (layerCounter > 0) {
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


            int canvasLayerSize = (int) canvasLayers.getWidth(); // SQUERE
            int imageLayerHeight = (int) canvasLayerDimension.getHeight();
            canvasLayers.setHeight(layerCounter * canvasLayerSize);

            int padding = (canvasLayerSize - imageLayerHeight) / 2;
            int paddingX = (int)((image.getWidth() / k)<canvasLayerSize?(canvasLayerSize-(image.getWidth() / k))/2:0);
            for (int i = 0; i < layerCounter; i++) {
                canvasLayers.getGraphicsContext2D().drawImage(this.image, paddingX, padding + i * canvasLayerSize, canvasLayerDimension.getWidth(), imageLayerHeight);
                canvasLayers.getGraphicsContext2D().drawImage(layers.get(i), paddingX, padding + i * canvasLayerSize, canvasLayerDimension.getWidth(), imageLayerHeight);
                canvasLayers.getGraphicsContext2D().strokeRoundRect(0, 0 + i * canvasLayerSize, canvasLayerSize, canvasLayerSize, 10, 10);
            }
        }
    }

    public void redrawCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        canvas.setHeight(this.image.getHeight());
        canvas.setWidth(this.image.getWidth());
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(this.image, 0, 0, canvas.getWidth(), canvas.getHeight());
        for (int i = 0; i < visibleLayers.size(); i++) {
            gc.drawImage(visibleLayers.get(i), 0, 0);
        }
    }

    public void redrawCanvasMap() {
        WritableImage image = canvas.snapshot(null, null);
        GraphicsContext gc = canvasMap.getGraphicsContext2D();
        Dimension canvasMapDimension = new Dimension(0, 0);
        if (image.getWidth() > gc.getCanvas().getWidth()) {
            double k = image.getWidth() / (gc.getCanvas().getWidth());
            canvasMapDimension.setSize(image.getWidth() / k, image.getHeight() / k);
        } else if (image.getHeight() > gc.getCanvas().getHeight()) {
            double k = image.getHeight() / (gc.getCanvas().getHeight());
            canvasMapDimension.setSize(image.getWidth() / k, image.getHeight() / k);

        }
        gc.drawImage(image, (gc.getCanvas().getWidth() - canvasMapDimension.getWidth()) / 2 + 1, (gc.getCanvas().getHeight() - canvasMapDimension.getHeight()) / 2 + 1, canvasMapDimension.getWidth() - 2, canvasMapDimension.getHeight() - 2);
    }

    public void saveMaskandStep(ActionEvent ae) {
        save(ae);
        ObservableList<String> selected = listViewFileList.getSelectionModel().getSelectedItems();
        int selectedIndex = listViewFileList.getItems().indexOf(selected.get(0));
        if (selectedIndex + 1 < listViewFileList.getItems().size())
            listViewFileList.getSelectionModel().select(listViewFileList.getItems().get(listViewFileList.getItems().indexOf(selected.get(0)) + 1));

    }

    public void save(ActionEvent ae) {
        if(maskPath==null)chooseMaskRoot(ae);
        if (maskPath==null)return;
        File directory = new File(maskPath.toString());
        new File(maskPath.toString()+"\\img").mkdir();
        new File(maskPath.toString()+"\\mask").mkdir();
        Long maxLong = maxNumberLongName(maskPath.toString()+"\\img");
        maxLong++;
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage saveImage = new WritableImage(width, height);
        PixelWriter writer = saveImage.getPixelWriter();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                    writer.setColor(x, y, Color.BLACK);
            }
        }
        for(int i=0;i<layers.size();i++) {
            PixelReader layer = layers.get(i).getPixelReader();
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    Color maskColor = layer.getColor(x, y);
                    if (maskColor.equals(Color.WHITE)) {
                        writer.setColor(x, y, Color.WHITE);
                    }
                }
            }
        }

        RenderedImage maskImage = SwingFXUtils.fromFXImage(saveImage, null);
        RenderedImage mainImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(mainImage, "png", new File(maskPath.toString() + "\\img\\" + maxLong+".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ImageIO.write(maskImage, "png", new File(maskPath.toString() + "\\mask\\" + maxLong+".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void activateDeactivateControlls(boolean enable){
        saveFragment.setDisable(!enable);
        saveAndNext.setDisable(!enable);
        save.setDisable(!enable);
    }

    private Long maxNumberLongName(String path){
        File imgDirectory = new File(path);
        File[] files = imgDirectory.listFiles();
        Long maxLong = -1l;
        if(files!=null) {
            for (File file : files) {
                String nameWithoutExtension = getNameWithoutExtension(file.getName());
                String extension = getExtensionByStringHandling(file.getName());
                if (file.isFile() && Main.format.contains(extension) && isLong(nameWithoutExtension)) {
                    Long curLong = Long.parseLong(nameWithoutExtension);
                    if (maxLong < curLong) maxLong = curLong;
                }
            }
        }
        return maxLong;
    }

    public void saveFragment(ActionEvent ae){
        if(maskPath==null)chooseMaskRoot(ae);
        if (maskPath==null)return;
        File directory = new File(maskPath.toString());
        new File(maskPath.toString()+"\\img-p").mkdir();
        new File(maskPath.toString()+"\\mask-p").mkdir();
        Long maxLong = maxNumberLongName(maskPath.toString()+"\\img-p");
        maxLong++;

        int widthP = (int) paneCanvas.getWidth();
        int heightP = (int) paneCanvas.getHeight();
        int widthC = (int) canvas.getWidth();
        int heightC = (int) canvas.getHeight();
        int widthI = (int) image.getWidth();
        int heightI = (int) image.getHeight();
        int width = widthI<widthP?widthI:widthP;
        int height = heightI<heightP?heightI:heightP;

        int imgPositionX = (int) ((canvas.getWidth() - width) * paneCanvas.getHvalue());
        int imgPositionY = (int) ((canvas.getHeight() - height) * paneCanvas.getVvalue());
        WritableImage saveImage = new WritableImage(width, height);
        PixelWriter writer = saveImage.getPixelWriter();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                writer.setColor(x, y, Color.BLACK);
            }
        }
        for(int i=0;i<layers.size();i++) {
            PixelReader layer = layers.get(i).getPixelReader();
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    Color maskColor = layer.getColor(imgPositionX+x, imgPositionY+y);
                    if (maskColor.equals(Color.WHITE)) {
                        writer.setColor(x, y, Color.WHITE);
                    }
                }
            }
        }
        RenderedImage maskImage = SwingFXUtils.fromFXImage(saveImage, null);
        PixelReader reader = image.getPixelReader();
        WritableImage newImage = new WritableImage(reader, imgPositionX, imgPositionY, width, height);
        RenderedImage mainImage = SwingFXUtils.fromFXImage(newImage, null);
        try {
            ImageIO.write(mainImage, "png", new File(maskPath.toString() + "\\img-P\\" + maxLong+".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ImageIO.write(maskImage, "png", new File(maskPath.toString() + "\\mask-P\\" + maxLong+".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
