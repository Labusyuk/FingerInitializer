package sample.entity;

import javafx.scene.image.Image;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class Layer {
    private Image image;
    private boolean visible;
}
