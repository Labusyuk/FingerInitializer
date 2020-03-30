package sample.modules;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.io.IOException;

public class MyMenuBar {
    MenuItem menuBarChangeRootPath;
    MenuItem menuBarChangeMaskPath;
    MenuItem menuBarChangeHelp;

    public MyMenuBar(MenuItem menuBarChangeRootPath, MenuItem menuBarChangeMaskPath, MenuItem menuBarChangeHelp) {
        this.menuBarChangeRootPath = menuBarChangeRootPath;
        this.menuBarChangeMaskPath = menuBarChangeMaskPath;
        this.menuBarChangeHelp = menuBarChangeHelp;
    }
}
