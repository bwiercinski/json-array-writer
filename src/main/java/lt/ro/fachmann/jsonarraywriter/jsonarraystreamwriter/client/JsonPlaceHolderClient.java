package lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.exception.JsonPlaceHolderClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class JsonPlaceHolderClient {

    private final WebClient jsonPlaceHolderWebClient;

    @Autowired
    public JsonPlaceHolderClient(WebClient jsonPlaceHolderWebClient) {
        this.jsonPlaceHolderWebClient = jsonPlaceHolderWebClient;
    }

    public Flux<ObjectNode> invokeApi(String endPoint) {
        return jsonPlaceHolderWebClient.get()
            .uri(endPoint)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToFlux(ObjectNode.class)
            .onErrorMap(e -> new JsonPlaceHolderClientException("Error while processing invokeApi for " + endPoint, e));
    }
}
