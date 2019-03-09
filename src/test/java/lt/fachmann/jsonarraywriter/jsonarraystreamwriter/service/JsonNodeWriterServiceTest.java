package lt.fachmann.jsonarraywriter.jsonarraystreamwriter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lt.fachmann.jsonarraywriter.jsonarraystreamwriter.model.WritingStatusCode;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class JsonNodeWriterServiceTest {

    private final String tempDirectoryName = "json-node-writer-test";
    private final File tempDirectory = new File(new File(System.getProperty("java.io.tmpdir")), tempDirectoryName);

    @InjectMocks
    private JsonNodeWriterService service = new JsonNodeWriterService(tempDirectory);

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

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
        File subDirectory = service.createSubDirectory(subDirectoryName);

        // then
        assertThat(tempDirectory.listFiles()).contains(new File(tempDirectory, subDirectoryName));
        String fileSeparatorInPattern = Pattern.quote(File.separator);
        String datePattern = "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}.[0-9]{2}.[0-9]{2}.[0-9]{3}Z";
        assertThat(subDirectory.getAbsolutePath())
            .matches(".*json-node-writer-test" + fileSeparatorInPattern + subDirectoryName + fileSeparatorInPattern + subDirectoryName + "_" + datePattern);
    }

    @Test
    public void writeValue_test() throws IOException {
        // given
        String subDirectoryName = "test2";
        ObjectNode testPost = objectMapper.readValue(getClass().getClassLoader().getResourceAsStream("posts/test-post.json"), ObjectNode.class);

        // when
        WritingStatusCode writingStatusCode = service.writeValue(subDirectoryName, tempDirectory, testPost);

        // then
        assertThat(writingStatusCode).isEqualTo(WritingStatusCode.SUCCESS);
        assertThat(tempDirectory.listFiles()).contains(new File(tempDirectory, subDirectoryName + "-95.json"));
    }

    @Test
    public void writeValue_testWhenNoId() throws IOException {
        // given
        String subDirectoryName = "test3";
        ObjectNode testPost = objectMapper.readValue(getClass().getClassLoader().getResourceAsStream("posts/test-post-without-id.json"), ObjectNode.class);

        // when
        WritingStatusCode writingStatusCode = service.writeValue(subDirectoryName, tempDirectory, testPost);

        // then
        assertThat(writingStatusCode).isEqualTo(WritingStatusCode.SUCCESS);
        assertThat(tempDirectory.listFiles()).hasSize(1);
        assertThat(tempDirectory.listFiles()[0].getName()).matches(subDirectoryName + "-[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.json");
    }
}