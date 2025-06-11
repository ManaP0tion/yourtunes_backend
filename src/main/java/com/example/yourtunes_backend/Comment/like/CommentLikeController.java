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
    public ResponseEntity<CommentLike> likeComment(
            @PathVariable Long commentId,
            @RequestParam String username
    ) {
        Comment comment = new Comment();
        comment.setCommentId(commentId);

        CommentLike commentLike = new CommentLike();
        commentLike.setComment(comment);
        commentLike.setUserId(username);

        return ResponseEntity.ok(commentLikeRepository.save(commentLike));
    }

    @GetMapping("/{commentId}/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable Long commentId) {
        long count = commentLikeRepository.countByComment_CommentId(commentId);
        return ResponseEntity.ok(count);
    }
}