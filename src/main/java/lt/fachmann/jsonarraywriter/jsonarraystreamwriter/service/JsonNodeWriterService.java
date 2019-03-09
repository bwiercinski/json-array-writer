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

    private File exportDirectory;

    public JsonNodeWriterService(@Value("${lt.ro.fachmann.jsonarraywriter.export-directory:.}") File exportDirectory) {
        this.exportDirectory = exportDirectory;
    }

    public WritingStatus write(String requestName, File subDirectory, JsonNode object) {
        return new WritingStatus(writeValue(requestName, subDirectory, object), object);
    }

    public WritingStatusCode writeValue(String requestName, File subDirectory, JsonNode object) {
        try {
            objectMapper.writeValue(resolveFileName(subDirectory, requestName, object), object);
            return WritingStatusCode.SUCCESS;
        } catch (IOException e) {
            log.error("Failed writing " + object + " into " + subDirectory + ". Cause: " + e.getMessage(), e);
            return WritingStatusCode.FAILED;
        }
    }

    public File createSubDirectory(String name) {
        File endpointDirectory = new File(exportDirectory, name);
        File subDirectory = new File(endpointDirectory, name + "_" + LocalDateTime.now().format(directoryDateTimeFormatter));
        subDirectory.mkdirs();
        return subDirectory;
    }

    private File resolveFileName(File subDirectory, String requestName, JsonNode jsonNode) {
        String fileName = String.format("%s-%s.json", requestName, Optional.ofNullable(jsonNode.findValue("id"))
            .map(JsonNode::asText)
            .orElse(UUID.randomUUID().toString()));
        return new File(subDirectory, fileName);
    }
}
