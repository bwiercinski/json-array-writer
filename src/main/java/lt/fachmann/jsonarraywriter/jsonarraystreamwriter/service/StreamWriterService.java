package lt.fachmann.jsonarraywriter.jsonarraystreamwriter.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import lt.fachmann.jsonarraywriter.jsonarraystreamwriter.model.WritingStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.File;

@Service
@Slf4j
public class StreamWriterService {

    @Autowired
    @Qualifier("jsonPlaceHolderWebClient")
    private WebClient jsonPlaceHolderWebClient;

    @Autowired
    private JsonNodeWriterService jsonNodeWriterService;

    public Flux<WritingStatus> executeWriting(String endPoint) {
        File subDirectory = jsonNodeWriterService.createSubDirectory(endPoint);

        log.info("Invoking executeWriting for subDirectory: " + subDirectory);

        return jsonPlaceHolderWebClient.get()
            .uri("/" + endPoint)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToFlux(ObjectNode.class)
            .map(object -> jsonNodeWriterService.write(endPoint, subDirectory, object))
            .log();
    }
}
