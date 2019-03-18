package lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.service;

import lombok.extern.slf4j.Slf4j;
import lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.client.JsonPlaceHolderClient;
import lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.model.Comment;
import lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.model.Post;
import lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.model.WritingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PostService {
    private final String DIRECTORY_NAME = "posts";

    private final JsonPlaceHolderClient jsonPlaceHolderClient;

    private final JsonNodeWriterService jsonNodeWriterService;

    private final String postsEndpoint;
    private final String commentsEndpoint;

    @Autowired
    public PostService(JsonPlaceHolderClient jsonPlaceHolderClient,
        JsonNodeWriterService jsonNodeWriterService,
        @Value("${lt.ro.fachmann.jsonarraywriter.posts-endpoint}") String postsEndpoint,
        @Value("${lt.ro.fachmann.jsonarraywriter.comments-endpoint}") String commentsEndpoint) {
        this.jsonPlaceHolderClient = jsonPlaceHolderClient;
        this.jsonNodeWriterService = jsonNodeWriterService;
        this.postsEndpoint = postsEndpoint;
        this.commentsEndpoint = commentsEndpoint;
    }

    public Flux<WritingResult<Post>> executeWritingForPosts() {
        return jsonNodeWriterService.createSubDirectory(DIRECTORY_NAME)
            .doOnSuccess(subDirectory -> log.info("Invoking executeWriting for subDirectory: " + subDirectory))
            .flux()
            .flatMap(subDirectory -> getPosts()
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(object -> jsonNodeWriterService.write(subDirectory, object)))
            .log();
    }

    public Flux<Post> getPosts() {
        return Mono.zip(
            jsonPlaceHolderClient.invokeApi(postsEndpoint, new ParameterizedTypeReference<List<Post>>() {
            }),
            jsonPlaceHolderClient.invokeApi(commentsEndpoint, new ParameterizedTypeReference<List<Comment>>() {
            })
        ).map(this::joinComments)
            .flatMapMany(Flux::fromIterable);
    }

    private List<Post> joinComments(Tuple2<List<Post>, List<Comment>> objects) {
        Map<Long, List<Comment>> commentsByPostId = objects.getT2().stream()
            .collect(Collectors.groupingBy(Comment::getPostId));
        return objects.getT1().stream()
            .map(post -> post.withComments(commentsByPostId.getOrDefault(post.getId(), null)))
            .collect(Collectors.toList());
    }
}
