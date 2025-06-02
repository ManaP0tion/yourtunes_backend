package com.example.yourtunes_backend.Comment.like;

import com.example.yourtunes_backend.Comment.Comment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment-likes")
public class CommentLikeController {
    private final CommentLikeRepository commentLikeRepository;

    public CommentLikeController(CommentLikeRepository commentLikeRepository) {
        this.commentLikeRepository = commentLikeRepository;
    }

    @PostMapping("/{commentId}")
    public ResponseEntity<CommentLike> likeComment(@PathVariable Long commentId, @RequestBody CommentLike commentLike) {
        Comment comment = new Comment();
        comment.setCommentId(commentId);
        commentLike.setComment(comment);
        return ResponseEntity.ok(commentLikeRepository.save(commentLike));
    }
}