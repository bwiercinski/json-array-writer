package lt.fachmann.jsonarraywriter.jsonarraystreamwriter.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WritingStatus {
    private WritingStatusCode code;
    private JsonNode object;
}
