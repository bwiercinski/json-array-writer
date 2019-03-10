package lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.model.WritingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class JsonNodeWriterService {

    private static final DateTimeFormatter directoryDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH.mm.ss.SSS'Z'");

    private final ObjectMapper objectMapper;

    private final File exportDirectory;

    @Autowired
    public JsonNodeWriterService(ObjectMapper objectMapper,
        @Value("${lt.ro.fachmann.jsonarraywriter.export-directory:.}") File exportDirectory) {
        this.objectMapper = objectMapper;
        this.exportDirectory = exportDirectory;
    }

    public Mono<WritingResult> write(String requestName, File subDirectory, JsonNode object) {
        return writeValue(requestName, subDirectory, object)
            .map(status -> new WritingResult(status, object));
    }

    public Mono<WritingResult.Status> writeValue(String requestName, File subDirectory, JsonNode object) {
        return Mono.fromCallable(() -> {
            try {
                File targetFile = new File(subDirectory, resolveFileName(requestName, object));
                objectMapper.writeValue(targetFile, object);
                return WritingResult.Status.SUCCESS;
            } catch (IOException e) {
                log.error("Failed writing " + object + " into " + subDirectory + ". Cause: " + e.getMessage(), e);
                return WritingResult.Status.FAILED;
            }
        });
    }

    public Mono<File> createSubDirectory(String name) {
        return Mono.fromCallable(() -> {
            File endpointDirectory = new File(exportDirectory, name);
            File subDirectory = new File(endpointDirectory, name + "_" + LocalDateTime.now().format(directoryDateTimeFormatter));
            subDirectory.mkdirs();
            return subDirectory;
        });
    }

    private String resolveFileName(String requestName, JsonNode jsonNode) {
        return String.format("%s-%s.json", requestName, Optional.ofNullable(jsonNode.findValue("id"))
            .map(JsonNode::asText)
            .orElse(UUID.randomUUID().toString()));
    }
}
