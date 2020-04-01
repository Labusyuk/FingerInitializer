package sample.controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;

public class HelpController {
    @FXML
    private ImageView cyberPoliceImg;
    @FXML
    private ImageView cyberSecurityImg;
    @FXML
    public void initialize(){
        Image image1 = new Image(String.valueOf(getClass().getResource("/images/Cyberpolice.png")));
        cyberPoliceImg.setImage(image1);
        Image image2 = new Image(String.valueOf(getClass().getResource("/images/CyberSecurity.png")));
        cyberSecurityImg.setImage(image2);
    }
}
