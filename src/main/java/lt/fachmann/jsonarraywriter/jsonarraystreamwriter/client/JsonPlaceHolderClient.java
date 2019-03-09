package lt.fachmann.jsonarraywriter.jsonarraystreamwriter.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lt.fachmann.jsonarraywriter.jsonarraystreamwriter.exception.JsonPlaceHolderClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class JsonPlaceHolderClient {

    @Autowired
    private WebClient jsonPlaceHolderWebClient;

    public Flux<ObjectNode> invokeApi(String endPoint) {
        return jsonPlaceHolderWebClient.get()
            .uri(endPoint)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToFlux(ObjectNode.class)
            .onErrorMap(throwable -> {
                throw new JsonPlaceHolderClientException("Error while processing invokeApi for " + endPoint, throwable);
            });
    }
}
