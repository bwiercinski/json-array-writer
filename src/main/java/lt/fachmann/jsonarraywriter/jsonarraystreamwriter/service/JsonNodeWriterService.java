package lt.fachmann.jsonarraywriter.jsonarraystreamwriter.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lt.fachmann.jsonarraywriter.jsonarraystreamwriter.model.WritingStatus;
import lt.fachmann.jsonarraywriter.jsonarraystreamwriter.model.WritingStatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class JsonNodeWriterService {

    private final static DateTimeFormatter directoryDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH.mm.ss.SSS'Z'");

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${lt.ro.fachmann.jsonarraywriter.export-directory:.}")
    private File exportDirectory;

    public WritingStatus write(String endPoint, File subDirectory, JsonNode object) {
        return new WritingStatus(writeValue(endPoint, subDirectory, object), object);
    }

    public WritingStatusCode writeValue(String endPoint, File subDirectory, JsonNode object) {
        try {
            objectMapper.writeValue(resolveFileName(subDirectory, endPoint, object), object);
            return WritingStatusCode.SUCCESS;
        } catch (IOException e) {
            log.error("Failed writing " + object + " into " + subDirectory + ". Cause: " + e.getMessage(), e);
            return WritingStatusCode.FAILED;
        }
    }

    public File createSubDirectory(String endPoint) {
        File endpointDirectory = new File(exportDirectory, endPoint);
        endpointDirectory.mkdir();
        File subDirectory = new File(endpointDirectory, endPoint + "_" + LocalDateTime.now().format(directoryDateTimeFormatter));
        subDirectory.mkdir();
        return subDirectory;
    }

    private File resolveFileName(File subDirectory, String endPoint, JsonNode jsonNode) {
        String fileName = endPoint + Optional.ofNullable(jsonNode.findValue("id"))
            .map(JsonNode::asText)
            .orElse(UUID.randomUUID().toString()) + ".json";
        return new File(subDirectory, fileName);
    }
}
