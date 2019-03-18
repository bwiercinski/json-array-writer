package lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.controller;

import lombok.extern.slf4j.Slf4j;
import lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.model.Post;
import lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.model.WritingResult;
import lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@Slf4j
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping(path = "posts", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Post> getPosts() {
        return postService.getPosts();
    }

    @GetMapping(path = "writePosts", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<WritingResult<Post>> executeWritingForPosts() {
        return postService.executeWritingForPosts();
    }
}
