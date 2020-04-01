package sample.modules;

import javafx.scene.control.TreeView;
import lombok.AllArgsConstructor;
import lombok.Data;
import sample.entity.Result;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class TreeViewCompleted {
    private TreeView treeViewCompleted;
    private List<Result> completed = new ArrayList<>();
    private String outputPath = new String();

    private void update(){

    }

    public void addResult(Result result){
        if(result!=null) {
            updateDataFromExternalSorage();
            completed.stream().forEach((x) -> {
                if (x.getSourceName().equals(result.getSourceName())) {
                    x.getImg().addAll(result.getImg());
                    x.getImg_p().addAll(result.getImg_p());
                }
            });
            update();
            saveDataToExternalStorage();
        }
    }

    private void updateDataFromExternalSorage(){

    }

    private void saveDataToExternalStorage(){

    }
}
