package sample.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
public class Result {
    private String sourceName;
    private Set<Image> mask = new HashSet<>();
    private Set<Image> mask_p = new HashSet<>();

    public Result() {
    }

    public Result(String sourceName, Set<Image> mask, Set<Image> mask_p) {
        this.sourceName = sourceName;
        if (mask != null)
            this.mask = mask;
        else this.mask = new HashSet<>();
        if (mask_p != null)
            this.mask_p = mask_p;
        else this.mask_p = new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result result = (Result) o;
        return sourceName.equals(result.sourceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceName);

    }
}
