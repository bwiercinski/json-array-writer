package lt.fachmann.jsonarraywriter.jsonarraystreamwriter.controller;

import lombok.extern.slf4j.Slf4j;
import lt.fachmann.jsonarraywriter.jsonarraystreamwriter.model.WritingStatus;
import lt.fachmann.jsonarraywriter.jsonarraystreamwriter.service.StreamWriterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Optional;

@RestController
@Slf4j
public class StreamWriterController {

    @Autowired
    private StreamWriterService streamWriterService;

    @GetMapping(path = "executeWriting", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<WritingStatus> executeWriting(@PathVariable Optional<String> maybeEndpoint) {
        return streamWriterService.executeWriting(maybeEndpoint.orElse("posts"));
    }
}
