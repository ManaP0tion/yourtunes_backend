package com.example.yourtunes_backend.Comment.like;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    long countByComment_CommentId(Long commentId);
}