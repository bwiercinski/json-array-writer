package lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.service;

import lombok.extern.slf4j.Slf4j;
import lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.client.JsonPlaceHolderClient;
import lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.model.WritingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
public class StreamWriterService {

    private final JsonPlaceHolderClient jsonPlaceHolderClient;

    private final JsonNodeWriterService jsonNodeWriterService;

    @Autowired
    public StreamWriterService(JsonPlaceHolderClient jsonPlaceHolderClient, JsonNodeWriterService jsonNodeWriterService) {
        this.jsonPlaceHolderClient = jsonPlaceHolderClient;
        this.jsonNodeWriterService = jsonNodeWriterService;
    }

    public Flux<WritingResult> executeWriting(String name, String endPoint) {
        return jsonNodeWriterService.createSubDirectory(name)
            .doOnSuccess(subDirectory -> log.info("Invoking executeWriting for subDirectory: " + subDirectory))
            .flux()
            .flatMap(subDirectory -> jsonPlaceHolderClient.invokeApi(endPoint)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(object -> jsonNodeWriterService.write(name, subDirectory, object)))
            .log();
    }
}
