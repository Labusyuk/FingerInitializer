package sample.modules;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import sample.entity.Image;
import sample.entity.Result;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Data
public class TreeViewCompleted {
    private TreeView treeViewCompleted;
    private List<Result> completed = new ArrayList<>();
    private String outputPath;

    public TreeViewCompleted(TreeView treeViewCompleted) {
        this.treeViewCompleted = treeViewCompleted;
    }

    private void update(){
        TreeItem<String> rootItem = new TreeItem<String>("Переглянути");
        rootItem.setExpanded(true);
        for(int i = 0; i<completed.size();i++){
            Result result = completed.get(i);

            TreeItem<String> resultItem = new TreeItem<String>(result.getSourceName());
            if (i != completed.size() - 1)
                resultItem.setExpanded(false);
            else
                resultItem.setExpanded(true);

            TreeItem<String> maskItem = new TreeItem<String>("mask");
            maskItem.setExpanded(true);
            for(Image mask:result.getMask()){
                TreeItem<String> imageItem = new TreeItem<String>(mask.getTargetName());
                maskItem.getChildren().add(imageItem);
            }
            TreeItem<String> mask_pItem = new TreeItem<String>("mask-p");
            mask_pItem.setExpanded(true);
            for(Image mask_p:result.getMask_p()){
                TreeItem<String> imageItem = new TreeItem<String>(mask_p.getTargetName());
                mask_pItem.getChildren().add(imageItem);
            }
            if(!maskItem.getChildren().isEmpty() && !mask_pItem.getChildren().isEmpty())
                resultItem.getChildren().addAll(maskItem,mask_pItem);
            if(!maskItem.getChildren().isEmpty() && mask_pItem.getChildren().isEmpty())
                resultItem.getChildren().addAll(maskItem);
            if(maskItem.getChildren().isEmpty() && !mask_pItem.getChildren().isEmpty())
                resultItem.getChildren().addAll(mask_pItem);
            rootItem.getChildren().add(resultItem);
        }
        treeViewCompleted.setRoot(rootItem);

    }

    public void addResult(Result result){
        if(result!=null) {
            updateDataFromExternalSorage();
            completed.stream().forEach((x) -> {
                if (x.getSourceName().equals(result.getSourceName())) {
                    x.getMask().addAll(result.getMask());
                    x.getMask_p().addAll(result.getMask_p());
                }
            });
            if(completed.contains(result)){
                completed.get(completed.indexOf(result)).getMask().addAll(result.getMask());
                completed.get(completed.indexOf(result)).getMask_p().addAll(result.getMask_p());
            }else {
                completed.add(result);
            }
            update();
            saveDataToExternalStorage();
        }
    }

    private void updateDataFromExternalSorage(){
        File result = new File(outputPath.toString()+"\\result.json");
        if(!result.exists())
            return;
        try {
            // JSON file to Java object
            TypeReference<List<Result>> typeReference = new TypeReference<List<Result>>(){};
            ObjectMapper mapper = new ObjectMapper();
            completed = mapper.readValue(result,typeReference);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDataToExternalStorage(){
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File(outputPath.toString()+"\\result.json"), completed);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*
    public static void main(String[] args) {
        TreeViewCompleted treeViewCompleted = new TreeViewCompleted();
        treeViewCompleted.setOutputPath("M:\\MyProjects\\Cyber Police Finger Initializer\\Out");
        treeViewCompleted.addResult(new Result("img_02.jpj",new HashSet<>(Arrays.asList(new Image("0",0,0,20,50),new Image("2",0,0,80,90))),new HashSet<>(Arrays.asList(new Image("25",8,9,10,20),new Image("26",10,20,20,31)))));
    }*/
}
