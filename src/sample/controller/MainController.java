package sample.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
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
import sample.modules.*;
import sun.awt.image.ToolkitImage;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
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
    private MyMenuBar myMenuBar;
    private Dimension maxImageDimension;
    private Dimension workPlaceDimension = new Dimension(1000,1000);
    private Dimension dimension = new Dimension();
    private double koef;
    @FXML
    private ToggleButton buttonSelection;
    @FXML
    private Button buttonSaveSelection;
    @FXML
    private Button buttonSaveVisible;
    @FXML
    private Button buttonSave;
    @FXML
    private Button buttonSaveAndNext;
    @FXML
    private Button buttonNext;
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
    Label labelXY;
    @FXML
    Label labelListInputNM;
    @FXML
    Label labelListErrorN;
    @FXML
    ListView<String> inputList;
    @FXML
    ListView<String> errorList;
    private ListFile listFile;

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
    Canvas tempCanvas = new Canvas(100,100);
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
        listFile = new ListFile(this, inputList, errorList, labelListInputNM, labelListErrorN, labelCurrentNum);
        layers = new Layers(canvasLayers, tempCanvas);
        myCanvas = new MyCanvas(canvas, tempCanvas, paneCanvas,layers, sliderStroke);
        map = new Map(canvasMap,canvas);
        myMenuBar = new MyMenuBar(menuBarChangeRootPath,menuBarChangeMaskPath,menuBarChangeHelp);
        sliderImageSize.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, //
                                Number oldValue, Number newValue) {
                Double differenceWidth = myCanvas.getMaxWorkPlaceDimension().getWidth()-myCanvas.getPaneCanvas().getWidth();
                Double differenceHeight = myCanvas.getMaxWorkPlaceDimension().getHeight()-myCanvas.getPaneCanvas().getHeight();
                if(differenceWidth>0 && differenceHeight>0){
                myCanvas.setWorkPlaceDimension(new Dimension((int)(myCanvas.getMaxWorkPlaceDimension().getWidth()-(differenceWidth*newValue.doubleValue()/100)),(int)(myCanvas.getMaxWorkPlaceDimension().getHeight()-(differenceHeight*newValue.doubleValue()/100))));
                myCanvas.resizeUpdate();
                    labelFirstKoef.setText(""+myCanvas.getKoefSize());
                    labelResolution.setText(""+(int)image.getWidth()+"x"+(int)image.getHeight());
                }
            }
        });
    }

    public void update(){
        String imageURI = listFile.getUrl();
        String selectedImage = listFile.getSelectedItem();
        labelCurrentImage.setText(selectedImage);
        do {
            try {
//            FileInputStream fileInputStream = new FileInputStream(new File(imageURI));
//            System.out.println(fileInputStream);
//            ImageInputStream imageInputStream = ImageIO.createImageInputStream(fileInputStream);
//            System.out.println(imageInputStream);
                BufferedImage bit = ImageIO.read(new File(imageURI));
                if(bit.getHeight() > 8000 || bit.getWidth() > 8000) {
                    listFile.replyCurrentToErrorList();
                    return;
                }
                this.image = new Image(new FileInputStream(new File(imageURI)));

            } catch (IOException e) {
                listFile.replyCurrentToErrorList();
                break;
            }
        }while (false);
        myCanvas.setImage(image);
        canvas.setDisable(false);
        map.update();
        layers.clear();
        setAccessibility(false);
        labelFirstKoef.setText(""+myCanvas.getKoefSize());
        labelResolution.setText(""+(int)image.getWidth()+"x"+(int)image.getHeight());
    }



    public void chooseRootPath(Event mouseEvent) {
        errorList.getItems().clear();
        layers.clear();
        selectedItems = null;
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

    public void chooseOutputPath(Event mouseEvent) {
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

    public void canvasMouseDragged(MouseEvent me) {
        if(buttonSelection.isSelected())
        myCanvas.selectionMove(me);
        else
        myCanvas.paint(me);

    }

    public void canvasMouseReleased(MouseEvent mouseEvent) {
        if(!buttonSelection.isSelected()) {
            WritableImage writableImage = tempCanvas.snapshot(null, null);
            layers.setImage(image);
            PixelReader maskReader = writableImage.getPixelReader();
            int width = (int) tempCanvas.getWidth();
            int height = (int) tempCanvas.getHeight();
            System.out.println("width=" + width + " height=" + height);
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
            layers.addLayer(new Layer(dest, true));
            layers.update();
            map.update();
            myCanvas.clearTempCanvas();
            setAccessibility(true);
        }else
            myCanvas.selectionReleased(mouseEvent);
            buttonSaveSelection.setDisable(false);
    }

    public void canvasLayersOnMouseClicked(MouseEvent mouseEvent) {
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
            while (clickedPosition > layers.getLayerDimension().getHeight()) {
                clickedPosition -= layers.getLayerDimension().getHeight();
                selectedItem++;
            }
            if (layers.isVisible(selectedItem))
                showMenuItem.setSelected(true);
            int finalSelectedItem = selectedItem;
            removeItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    layers.removeLayer(finalSelectedItem);
                    myCanvas.update();
                    map.update();
                    layers.update();
                    if(layers.isEmpty()) {
                        setAccessibility(false);
                        buttonSelection.setSelected(false);
                    }
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

    @FXML
    public void save(MouseEvent me) {
        if(outputPath==null)chooseOutputPath(me);
        if (outputPath==null)return;
        File directory = new File(outputPath.toString());
        new File(outputPath.toString()+"\\img").mkdir();
        new File(outputPath.toString()+"\\mask").mkdir();
        Long maxLong = maxNumberLongName(outputPath.toString()+"\\img");
        maxLong++;
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        BufferedImage saveImage = new BufferedImage(width, height,BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = saveImage.createGraphics();
        graphics2D.setColor(java.awt.Color.BLACK);
        graphics2D.fill(new Rectangle(0,0,width,height));
        graphics2D.dispose();
        for(int i=0;i<layers.getCountLayers();i++) {
            Layer layer = layers.getLayer(i);
            if(layer.isVisible()) {
                BufferedImage bufferedLayer = getScaledRGBInstance(layer.getImage(),(int)image.getWidth(),(int)image.getHeight());
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        java.awt.Color maskColor = new java.awt.Color(bufferedLayer.getRGB(x,y));
                        if (maskColor.getRGB()==java.awt.Color.WHITE.getRGB()) {
                            saveImage.setRGB(x,y,java.awt.Color.WHITE.getRGB());
                        }
                    }
                }
            }
        }

        RenderedImage mainImage = SwingFXUtils.fromFXImage(image, null);


        try {
            ImageIO.write(mainImage, "jpg", new File(outputPath.toString() + "\\img\\" + maxLong+".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ImageIO.write(saveImage, "png", new File(outputPath.toString() + "\\mask\\" + maxLong+".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void saveAndNext(MouseEvent me) {
        save(me);
        inputList.getSelectionModel().selectNext();
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

    @FXML
    public void canvasMousePressed(MouseEvent me){
        if(!buttonSelection.isSelected())
        myCanvas.paint(me);
        else myCanvas.selectionPressed(me);
    }

    @FXML
    public void canvasMouseMoved(MouseEvent me){
        labelXY.setText(""+(int)me.getX()+":"+(int)me.getY());
    }

    @FXML
    public void moveErrorFile(MouseEvent me){
        if(outputPath==null)chooseOutputPath(me);
        else
        listFile.moveErrorFile();
    }

    public void setAccessibility(boolean visibile){
        buttonSelection.setDisable(!visibile);
        buttonSelection.setSelected(false);
        buttonSaveSelection.setDisable(!visibile);
        //buttonSaveVisible.setDisable(!visibile);
        buttonSave.setDisable(!visibile);
        buttonSaveAndNext.setDisable(!visibile);
        buttonNext.setDisable(!visibile);
    }


    @FXML
    public void canvasNextMouseClicked(MouseEvent me){
        inputList.getSelectionModel().selectNext();
    }

    @FXML
    public void canvasUpdate(MouseEvent me){
        if(buttonSelection.isSelected())
            buttonSaveSelection.setDisable(false);
        else {
            buttonSaveSelection.setDisable(true);
            myCanvas.update();
        }
    }

    @FXML
    public void showHelpWindow(ActionEvent ae){
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

    static BufferedImage getScaledRGBInstance(Image sourceImg, int targetWidth, int targetHeight) {
        BufferedImage img = SwingFXUtils.fromFXImage(sourceImg,null);
        int type = (img.getTransparency() == Transparency.OPAQUE) ?
                BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;

            BufferedImage imgTemp = new BufferedImage(targetWidth, targetHeight, type);
            Graphics2D g2 = imgTemp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(img, 0, 0, targetWidth, targetHeight, null);
            g2.dispose();
        return imgTemp;
    }

}
