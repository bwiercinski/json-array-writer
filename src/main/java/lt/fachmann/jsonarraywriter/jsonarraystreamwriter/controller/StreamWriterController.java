package lt.fachmann.jsonarraywriter.jsonarraystreamwriter.controller;

import lombok.extern.slf4j.Slf4j;
import lt.fachmann.jsonarraywriter.jsonarraystreamwriter.model.WritingStatus;
import lt.fachmann.jsonarraywriter.jsonarraystreamwriter.service.StreamWriterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Optional;

@RestController
@Slf4j
public class StreamWriterController {

    @Autowired
    private StreamWriterService streamWriterService;

    @Value("${lt.ro.fachmann.jsonarraywriter.default-endpoint:https://jsonplaceholder.typicode.com/posts}")
    private String defaultEndpoint;

    @GetMapping(path = "executeWriting/{name}", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<WritingStatus> executeWriting(@PathVariable("name") String name, @RequestParam("endPoint") Optional<String> maybeEndpoint) {
        return streamWriterService.executeWriting(name, maybeEndpoint.orElse(defaultEndpoint));
    }
}
