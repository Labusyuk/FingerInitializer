package sample.entity;

import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Result {
    private String sourceName;
    private Set<String> img = new HashSet<>();
    private Set<String> img_p = new HashSet<>();
}
