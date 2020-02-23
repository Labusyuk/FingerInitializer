package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main extends Application {
    public static List<String> format = new ArrayList<>(Arrays.asList(".png",".jpg"));

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Finger initializer");
        primaryStage.setScene(new Scene(root, 750, 440));
        primaryStage.show();
        primaryStage.setResizable(false);
        PathChooser pathChooser = new PathChooser();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
