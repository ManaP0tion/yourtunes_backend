package com.example.yourtunes_backend.Post.like;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByUserIdAndPost_PostId(String userId, Long postId);
    void deleteByUserIdAndPost_PostId(String userId, Long postId);
    long countByPost_PostId(Long postId);
}
