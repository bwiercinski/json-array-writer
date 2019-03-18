package lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.client;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.exception.JsonPlaceHolderClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class JsonPlaceHolderClient {

    private final WebClient jsonPlaceHolderWebClient;

    @Autowired
    public JsonPlaceHolderClient(WebClient jsonPlaceHolderWebClient) {
        this.jsonPlaceHolderWebClient = jsonPlaceHolderWebClient;
    }

    public <T> Mono<T> invokeApi(String endPoint, ParameterizedTypeReference<T> typeReference) {
        return jsonPlaceHolderWebClient.get()
            .uri(endPoint)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(typeReference)
            .onErrorMap(e -> new JsonPlaceHolderClientException("Error while processing invokeApi for " + endPoint, e));
    }
}
