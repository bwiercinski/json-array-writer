package lt.fachmann.jsonarraywriter.jsonarraystreamwriter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lt.fachmann.jsonarraywriter.jsonarraystreamwriter.client.JsonPlaceHolderClient;
import lt.fachmann.jsonarraywriter.jsonarraystreamwriter.model.WritingStatus;
import lt.fachmann.jsonarraywriter.jsonarraystreamwriter.model.WritingStatusCode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class StreamWriterServiceTest {

    @Mock
    private JsonPlaceHolderClient jsonPlaceHolderClient;

    @Mock
    private JsonNodeWriterService jsonNodeWriterService;

    @InjectMocks
    private StreamWriterService service = new StreamWriterService();

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void executeWriting_test() throws IOException {
        // given
        ObjectNode testPost = objectMapper.readValue(getClass().getClassLoader().getResourceAsStream("posts/test-post.json"), ObjectNode.class);

        String requestName = "requestName";
        String endPoint = "endPoint";
        File subDirectory = new File("test");
        WritingStatus excepted = new WritingStatus(WritingStatusCode.SUCCESS, testPost);

        doReturn(subDirectory).when(jsonNodeWriterService).createSubDirectory(eq(requestName));
        doReturn(excepted).when(jsonNodeWriterService).write(eq(requestName), eq(subDirectory), any());
        doReturn(Flux.just(testPost)).when(jsonPlaceHolderClient).invokeApi(eq(endPoint));

        // when
        Flux<WritingStatus> writingStatusFlux = service.executeWriting(requestName, endPoint);

        // then
        writingStatusFlux.collectList().subscribe(actual -> assertThat(actual).containsExactly(excepted));
    }
}