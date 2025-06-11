package com.example.yourtunes_backend.Comment;

import com.example.yourtunes_backend.Post.Post;
import com.example.yourtunes_backend.Post.PostRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentController(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    @PostMapping("/{postId}")
    public ResponseEntity<Comment> addComment(
            @PathVariable Long postId,
            @RequestParam String username,
            @RequestBody String content
    ) {
        return postRepository.findById(postId)
                .map(post -> {
                    Comment comment = new Comment();
                    comment.setPost(post);
                    comment.setContent(content);
                    comment.setUserId(username);
                    comment.setCreatedAt(LocalDateTime.now());
                    return ResponseEntity.ok(commentRepository.save(comment));
                })
                .orElseGet(() -> ResponseEntity.status(400).body(null));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentRepository.findByPostPostId(postId));
    }
}