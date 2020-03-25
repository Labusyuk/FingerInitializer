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
import sample.entity.Layer;
import sample.modules.Layers;
import sample.modules.Map;
import sample.modules.MyCanvas;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class MainController {
    private String rootPath, maskPath;
    private static Image image;
    private String selectedItems;
    private Map map;
    private Layers layers;
    private MyCanvas myCanvas;
    private Dimension maxImageDimension;
    private double koef;
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
    Slider sliderImageSize;
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
    Text textSize;
    @FXML
    ListView<String> listViewFileList;
    @FXML
    Button saveAndNext;
    @FXML
    Button saveFragment;
    @FXML
    Button save;

    @FXML
    public void initialize() {
        map = new Map(canvasMap, canvas);
        layers = new Layers(canvasLayers,tempCanvas,image);
        myCanvas = new MyCanvas(canvas,image,layers);
        MultipleSelectionModel<String> langsSelectionModel = listViewFileList.getSelectionModel();
        langsSelectionModel.setSelectionMode(SelectionMode.MULTIPLE);
        sliderStroke.setMax(200);
        sliderStroke.setValue(50);
        listViewFileList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);


        // устанавливаем слушатель для отслеживания изменений
        langsSelectionModel.selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> changed, String oldValue, String newValue) {
                selectedItems = "";
                ObservableList<String> selected = langsSelectionModel.getSelectedItems();
                for (String item : selected) {
                    selectedItems += item;
                }
                image = new Image("file:///" + rootPath + "\\" + selectedItems);
                myCanvas.setImage(image);
                layers.setImage(image);
                map.update();
                textSize.setText("k="+myCanvas.getKoefSize());
                tempCanvas.getGraphicsContext2D().clearRect(0, 0, tempCanvas.getWidth(), tempCanvas.getHeight());
                layers.clear();
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
                Stage newWindow = new Stage();
                newWindow.setTitle("Про програму");
                newWindow.setScene(secondScene);

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
        String extension = index == -1 ? null : filename.substring(index).toLowerCase();
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
    }

    public void canvasPaintMouseReleased(MouseEvent me) {
        map.update();
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
        layers.addLayer(new Layer(dest,true));
        tempCanvas.getGraphicsContext2D().clearRect(0, 0, tempCanvas.getWidth(), tempCanvas.getHeight());
        layers.update();
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
        if (mouseEvent.getButton() == MouseButton.SECONDARY && layers.getCountLayers() > 0 && mouseEvent.getY() > 0 && mouseEvent.getY() < (canvasLayers.getWidth() + layers.getCountLayers() * canvasLayers.getWidth())) {
            selectedItem++;
            Node source = (Node) mouseEvent.getSource();
            Window theStage = source.getScene().getWindow();
            int clickedPosition = (int) mouseEvent.getY();
            while (clickedPosition > canvasLayers.getWidth()) {
                clickedPosition -= canvasLayers.getWidth();
                selectedItem++;
            }
            if (layers.isVisible(selectedItem))
                showMenuItem.setSelected(true);
            int finalSelectedItem = selectedItem;
            removeItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    layers.removeLayer(finalSelectedItem);
                    layers.update();
                    myCanvas.update();
                    map.update();
                    if(layers.isEmpty())activateDeactivateControlls(false);
                }
            });
            showMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (layers.isVisible(finalSelectedItem))
                        layers.getLayer(finalSelectedItem).setVisible(false);
                    else
                        layers.getLayer(finalSelectedItem).setVisible(true);
                    myCanvas.update();
                    map.update();
                }
            });
            contextMenu.show(theStage, mouseEvent.getScreenX(), mouseEvent.getScreenY());
        }
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
        int width = (int) myCanvas.getDimension().getWidth();
        int height = (int) myCanvas.getDimension().getHeight();
        WritableImage saveImage = new WritableImage(width, height);
        PixelWriter writer = saveImage.getPixelWriter();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                    writer.setColor(x, y, Color.BLACK);
            }
        }

        for(int i=0;i<layers.getCountLayers();i++) {
            PixelReader layer = layers.getLayer(i).getImage().getPixelReader();
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    Color maskColor = layer.getColor(x, y);
                    if (maskColor.equals(Color.WHITE)) {
                        writer.setColor(x, y, Color.WHITE);
                    }
                }
            }
        }
        BufferedImage maskImage = SwingFXUtils.fromFXImage(saveImage, null);
        BufferedImage newMaskImage = new BufferedImage((int)image.getWidth(),(int)image.getHeight(),BufferedImage.TYPE_INT_ARGB);
        newMaskImage.createGraphics().drawImage(maskImage,0,0,(int)image.getWidth(),(int)image.getHeight(),null);
        //RenderedImage mainImage = SwingFXUtils.fromFXImage(image, null);

       try {
           System.out.println(selectedItems);
           System.out.println(getExtensionByStringHandling(selectedItems));
//            Path originalPath = new File(rootPath.toString() + "\\" + selectedItems).toPath();
//            Path copied = new File(maskPath.toString() + "\\img\\" + maxLong+getExtensionByStringHandling(selectedItems)).toPath();
//            Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
            System.out.println(rootPath+"\\"+selectedItems);
            BufferedImage main = ImageIO.read(new File(rootPath + "\\" + selectedItems));
            ImageIO.write(main, "jpg", new File(maskPath.toString() + "\\img\\" + maxLong+".jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ImageIO.write(newMaskImage, "png", new File(maskPath.toString() + "\\mask\\" + maxLong+".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void activateDeactivateControlls(boolean enable){
        //saveFragment.setDisable(!enable);
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
        int widthI = (int) image.getWidth();
        int heightI = (int) image.getHeight();
        int width = widthI<widthP?widthI:widthP;
        int height = heightI<heightP?heightI:heightP;

        int imgPositionX = (int) ((canvas.getWidth() - width) * paneCanvas.getHvalue());
        int imgPositionY = (int) ((canvas.getHeight() - height) * paneCanvas.getVvalue());
        int k = myCanvas.getKoefSize()>1?(int)myCanvas.getKoefSize():1;

        WritableImage saveImage = new WritableImage(width, height);
        PixelWriter writer = saveImage.getPixelWriter();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                writer.setColor(x, y, Color.BLACK);
            }
        }
        for(int i=0;i<layers.getCountLayers();i++) {
            PixelReader layer = layers.getLayer(i).getImage().getPixelReader();
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    Color maskColor = layer.getColor(imgPositionX+x, imgPositionY+y);
                    if (maskColor.equals(Color.WHITE)) {
                        writer.setColor(x, y, Color.WHITE);
                    }
                }
            }
        }
        BufferedImage maskImage = SwingFXUtils.fromFXImage(saveImage, null);
        BufferedImage newMaskImage = new BufferedImage((int)(width*k),(int)(height*k),BufferedImage.TYPE_INT_ARGB);
        newMaskImage.createGraphics().drawImage(maskImage,0,0,(int)(width*k),(int)(height*k),null);
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
