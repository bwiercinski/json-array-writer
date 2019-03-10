package lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.model.WritingResult;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonNodeWriterServiceTest {

    private final String tempDirectoryName = "json-node-writer-test";
    private final File tempDirectory = new File(new File(System.getProperty("java.io.tmpdir")), tempDirectoryName);

    private ObjectMapper objectMapper = new ObjectMapper();

    private JsonNodeWriterService service = new JsonNodeWriterService(objectMapper, tempDirectory);

    @Before
    public void before() {
        tempDirectory.mkdirs();
    }

    @After
    public void after() throws IOException {
        FileUtils.deleteDirectory(tempDirectory);
    }

    @Test
    public void createSubDirectory_test() {
        // given
        String subDirectoryName = "test1";

        // when
        Mono<File> subDirectoryMono = service.createSubDirectory(subDirectoryName);

        // then
        subDirectoryMono.subscribe(subDirectory -> {
            assertThat(tempDirectory.listFiles()).contains(new File(tempDirectory, subDirectoryName));
            String fileSeparatorInPattern = Pattern.quote(File.separator);
            String datePattern = "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}.[0-9]{2}.[0-9]{2}.[0-9]{3}Z";
            assertThat(subDirectory.getAbsolutePath())
                .matches(".*json-node-writer-test" + fileSeparatorInPattern + subDirectoryName + fileSeparatorInPattern + subDirectoryName + "_" + datePattern);
        });
    }

    @Test
    public void writeValue_test() throws IOException {
        // given
        String subDirectoryName = "test2";
        ObjectNode testPost = objectMapper.readValue(getClass().getClassLoader().getResourceAsStream("posts/test-post.json"), ObjectNode.class);

        // when
        Mono<WritingResult.Status> statusMono = service.writeValue(subDirectoryName, tempDirectory, testPost);

        // then
        statusMono.subscribe(status -> {
            assertThat(status).isEqualTo(WritingResult.Status.SUCCESS);
            assertThat(tempDirectory.listFiles()).contains(new File(tempDirectory, subDirectoryName + "-95.json"));
        });
    }

    @Test
    public void writeValue_testWhenNoId() throws IOException {
        // given
        String subDirectoryName = "test3";
        ObjectNode testPost = objectMapper.readValue(getClass().getClassLoader().getResourceAsStream("posts/test-post-without-id.json"), ObjectNode.class);

        // when
        Mono<WritingResult.Status> statusMono = service.writeValue(subDirectoryName, tempDirectory, testPost);

        // then
        statusMono.subscribe(status -> {
            assertThat(status).isEqualTo(WritingResult.Status.SUCCESS);
            assertThat(tempDirectory.listFiles()).hasSize(1);
            assertThat(tempDirectory.listFiles()[0].getName()).matches(subDirectoryName + "-[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.json");
        });
    }
}