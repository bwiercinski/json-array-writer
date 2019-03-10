package lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WritingResult {

    public enum Status {
        SUCCESS, FAILED
    }

    private Status code;
    private JsonNode object;
}
