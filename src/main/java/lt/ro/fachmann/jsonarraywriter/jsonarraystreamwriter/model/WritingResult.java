package lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WritingResult<T> {

    public enum Status {
        SUCCESS, FAILED
    }

    private Status code;
    private T object;
}
