package lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.model.Post;
import lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.model.WritingResult;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
}