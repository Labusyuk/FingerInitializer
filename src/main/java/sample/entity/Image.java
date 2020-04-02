package sample.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Image {
    private String targetName;
    private int sourceX;
    private int sourceY;
    private int width;
    private int height;

}
