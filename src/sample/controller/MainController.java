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
import javafx.scene.control.Label;
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
import sample.listener.MainControllerUpdate;
import sample.modules.Layers;
import sample.modules.ListFile;
import sample.modules.Map;
import sample.modules.MyCanvas;
import sample.utill.BigBufferedImage;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
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


public class MainController implements MainControllerUpdate {
    private String inputPath, outputPath;
    private MultipleSelectionModel<String> langsSelectionModel;
    private static Image image;
    private String selectedItems;
    private Map map;
    private Layers layers;
    private MyCanvas myCanvas;
    private Dimension maxImageDimension;
    private double koef;
    @FXML
    Label labelInputPath;
    @FXML
    Label labelOutputPath;
    @FXML
    Label labelCurrentImage;
    @FXML
    Label labelCurrentNum;
    @FXML
    Label labelFirstKoef;
    @FXML
    Label labelResolution;

    @FXML
    ListView<String> inputList;
    private ListFile listFile;
//    @FXML
//    MenuItem menuBarChangeRootPath;
//    @FXML
//    MenuItem menuBarChangeMaskPath;
//    @FXML
//    MenuItem menuBarChangeHelp;
    @FXML
    Canvas canvasLayers;
//    @FXML
//    Slider sliderStroke;
//    @FXML
//    Slider sliderImageSize;
    @FXML
    ScrollPane paneCanvas;
    @FXML
    Canvas canvas;
    @FXML
    Canvas canvasMap;
//    Canvas tempCanvas = new Canvas(100, 100);
//    @FXML
//    Text textRootPath;
//    @FXML
//    Text textMaskPath;
//    @FXML
//    Text textSize;
//    @FXML
//    ListView<String> listViewFileList;
//    @FXML
//    Button saveAndNext;
//    @FXML
//    Button saveFragment;
//    @FXML
//    Button save;


    @FXML
    public void initialize() {
        System.out.println(  inputList.toString());
        listFile = new ListFile(this, inputList);
        layers = new Layers(canvasLayers);
        myCanvas = new MyCanvas(canvas,paneCanvas,layers);
        map = new Map(canvasMap,canvas);
    }

    public void update(){
        String imageURI = listFile.getUrl();
        String selectedImage = listFile.getSelectedItem();
        labelCurrentImage.setText(selectedImage);
        System.out.println(new File(imageURI));
        try {
            File file = new File(imageURI);
            System.out.println(file);
            ImageInputStream stream = ImageIO.createImageInputStream(file);
            System.out.println(stream);
            this.image = SwingFXUtils.toFXImage(BigBufferedImage.create(file,BufferedImage.TYPE_CUSTOM),null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ;
        myCanvas.setImage(image);
        map.update();
        labelFirstKoef.setText(""+myCanvas.getKoefSize());
        labelResolution.setText(""+(int)image.getWidth()+"x"+(int)image.getHeight());
    }

    public void chooseRootPath(MouseEvent mouseEvent) {
        Window theStage = null;
        if(mouseEvent.getSource() instanceof Node) {
            Node source = (Node) mouseEvent.getSource();
            theStage = source.getScene().getWindow();
        }else if(mouseEvent.getSource() instanceof MenuItem){
            ContextMenu contextMenu = ((MenuItem)(mouseEvent.getSource())).getParentPopup();
            theStage = contextMenu.getScene().getWindow();
        }
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Оберіть корінний шлях");
        File directory = directoryChooser.showDialog(theStage);
        if (directory != null) {
            List<String> fileList = new ArrayList<>();
            fileList.sort(Comparator.naturalOrder());
            inputPath = directory.toString();
            listFile.setInputPath(inputPath);
            labelInputPath.setText("" + inputPath);
            File[] files = directory.listFiles();
            for (File file : files)
                if (file.isFile() && Main.format.contains(getExtensionByStringHandling(file.getName()))) {
                    fileList.add(file.getName());
                }
            inputList.setEditable(false);
            inputList.setItems(FXCollections.observableList(fileList));
            if (0 < inputList.getItems().size()) {
                inputList.getSelectionModel().select(0);
//                canvas.setDisable(false);
            }
        }
    }

    public void chooseOutputPath(MouseEvent mouseEvent) {
        Window theStage = null;
        if(mouseEvent.getSource() instanceof Node) {
            Node source = (Node) mouseEvent.getSource();
            theStage = source.getScene().getWindow();
        }else if(mouseEvent.getSource() instanceof MenuItem){
            ContextMenu contextMenu = ((MenuItem)(mouseEvent.getSource())).getParentPopup();
            theStage = contextMenu.getScene().getWindow();
        }
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Оберіть вихідний шлях");
        File directory = directoryChooser.showDialog(theStage);
        if (directory != null) {
            outputPath = directory.toString();
            listFile.setOutputPath(outputPath);
            labelOutputPath.setText("" + outputPath);
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

    }

    public void canvasPaintMouseReleased(MouseEvent me) {
    }


    public void CanvasPaintMousePressed(MouseEvent mouseEvent) {
    }

    public void CanvasLayersOnMouseClicked(MouseEvent mouseEvent) {
    }

    public void save(ActionEvent ae) {
    }


    private void activateDeactivateControlls(boolean enable){
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

    public void saveFragment(ActionEvent ae) {
    }
}