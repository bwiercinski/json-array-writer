package lt.fachmann.jsonarraywriter.jsonarraystreamwriter.service;

import lombok.extern.slf4j.Slf4j;
import lt.fachmann.jsonarraywriter.jsonarraystreamwriter.client.JsonPlaceHolderClient;
import lt.fachmann.jsonarraywriter.jsonarraystreamwriter.model.WritingStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

@Service
@Slf4j
public class StreamWriterService {

    @Autowired
    private JsonPlaceHolderClient jsonPlaceHolderClient;

    @Autowired
    private JsonNodeWriterService jsonNodeWriterService;

    public Flux<WritingStatus> executeWriting(String endPoint) {
        File subDirectory = jsonNodeWriterService.createSubDirectory(endPoint);

        log.info("Invoking executeWriting for subDirectory: " + subDirectory);

        return jsonPlaceHolderClient.invokeApi(endPoint)
            .map(object -> jsonNodeWriterService.write(endPoint, subDirectory, object))
            .log();
    }
}
