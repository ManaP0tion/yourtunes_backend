package com.example.yourtunes_backend.Comment;

import com.example.yourtunes_backend.Post.Post;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentRepository commentRepository;

    public CommentController(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @PostMapping("/{postId}")
    public ResponseEntity<Comment> addComment(@PathVariable Long postId, @RequestBody Comment comment) {
        Post post = new Post();
        post.setPostId(postId);
        comment.setPost(post);
        return ResponseEntity.ok(commentRepository.save(comment));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentRepository.findByPostPostId(postId));
    }
}