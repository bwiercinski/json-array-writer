package lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.model.Post;
import lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.model.WritingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    public Mono<WritingResult<Post>> write(File subDirectory, Post post) {
        return writePost(subDirectory, post)
            .map(status -> new WritingResult<>(status, post));
    }

    public Mono<File> createSubDirectory(String name) {
        return Mono.fromCallable(() -> {
            File endpointDirectory = new File(exportDirectory, name);
            File subDirectory = new File(endpointDirectory, name + "_" + LocalDateTime.now().format(directoryDateTimeFormatter));
            subDirectory.mkdirs();
            return subDirectory;
        });
    }

    private Mono<WritingResult.Status> writePost(File subDirectory, Post post) {
        return Mono.fromCallable(() -> {
            try {
                File targetFile = new File(subDirectory, resolveFileName(post));
                objectMapper.writeValue(targetFile, post);
                return WritingResult.Status.SUCCESS;
            } catch (IOException e) {
                log.error("Failed writing post " + post.getId() + " into " + subDirectory + ". Cause: " + e.getMessage(), e);
                return WritingResult.Status.FAILED;
            }
        });
    }

    private String resolveFileName(Post post) {
        return "post_" + post.getId() + ".json";
    }
}
