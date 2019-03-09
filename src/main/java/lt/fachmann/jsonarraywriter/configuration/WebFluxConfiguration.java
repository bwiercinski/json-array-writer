package lt.fachmann.jsonarraywriter.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebFluxConfiguration {

    @Bean("jsonPlaceHolderWebClient")
    public WebClient webClient(WebClient.Builder builder) {
        return builder.baseUrl("https://jsonplaceholder.typicode.com").build();
    }
}
