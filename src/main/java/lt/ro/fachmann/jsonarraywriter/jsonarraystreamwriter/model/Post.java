package lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.model;

import lombok.Data;

import java.util.List;

@Data
public class Post {
    private final Long userId;
    private final Long id;
    private final String title;
    private final String body;
    private final List<Comment> comments;

    public Post withComments(List<Comment> comments) {
        return new Post(userId, id, title, body, comments);
    }
}
