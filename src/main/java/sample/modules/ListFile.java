package sample.modules;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import lombok.Data;
import sample.listener.MainControllerUpdate;

import java.awt.*;
import java.io.*;

@Data
public class ListFile {
    ListView<String> inputList;
    private MultipleSelectionModel<String> multipleSelectionModel;
    private ListView<String> listView;
    private ListView<String> errorList;
    private String url;
    private String inputPath = new String();
    private String outputPath = new String();
    private String selectedItem;
    Label labelListInputNM;
    Label labelListErrorN;
    Label labelCurrentNum;

    public ListFile(MainControllerUpdate mainControllerUpdate, ListView<String> inputList, ListView<String> errorList, Label labelListInputNM, Label labelListErrorN,Label labelCurrentNum) {
        this.inputList = inputList;
        this.errorList = errorList;
        this.labelListInputNM = labelListInputNM;
        this.labelListErrorN = labelListErrorN;
        this.labelCurrentNum = labelCurrentNum;
        multipleSelectionModel = inputList.getSelectionModel();
        inputList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        multipleSelectionModel.selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> changed, String oldValue, String newValue) {
                String selectedItems = "";
                ObservableList<String> selected = multipleSelectionModel.getSelectedItems();
                int selectedIndex = inputList.getSelectionModel().getSelectedIndex();
                labelListInputNM.setText(""+ (selectedIndex>=0?selectedIndex+1:0)+"/"+inputList.getItems().size());
                labelCurrentNum.setText(""+ (selectedIndex>=0?selectedIndex+1:0)+"/"+inputList.getItems().size());
                for (String item : selected) {
                    selectedItems += item;
                }
                url=inputPath + "\\" + selectedItems;
                selectedItem = selectedItems;
                mainControllerUpdate.update();
/*
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
*/

/*                GraphicsContext gc = canvas.getGraphicsContext2D();

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
                activateDeactivateControlls(false);*/
            }
        });
    }
    public void replyCurrentToErrorList(){
        int selectedIndex = inputList.getSelectionModel().getSelectedIndex();
        if (!selectedItem.isEmpty()) {
            errorList.getItems().add(selectedItem);
            if (selectedIndex + 1 < inputList.getItems().size()) {
                int index = inputList.getItems().indexOf(selectedItem);
                inputList.getSelectionModel().selectNext();
                inputList.getItems().remove(index);
            } else
                inputList.getItems().remove(inputList.getItems().indexOf(selectedItem));
        }
        labelListErrorN.setText(""+errorList.getItems().size());
    }

    public void moveErrorFile(){
        new File(outputPath.toString()+"\\error").mkdir();
        for(String strFile:errorList.getItems()){
            InputStream inStream = null;
            OutputStream outStream = null;
            int length;
            File afile =new File(inputPath + "\\" + strFile);
            File bfile =new File(outputPath + "\\error\\", strFile);
            try {
            inStream = new FileInputStream(afile);
            outStream = new FileOutputStream(bfile);
            byte[]buffer = new byte[1024];
            while ((length = inStream.read(buffer)) > 0){

                outStream.write(buffer, 0, length);
            }
                inStream.close();
                outStream.close();
                afile.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        errorList.getItems().clear();
    }



}
