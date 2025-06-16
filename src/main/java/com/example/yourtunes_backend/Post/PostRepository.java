package com.example.yourtunes_backend.Post;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(String userId);
    List<Post> findByTitleContainingIgnoreCase(String keyword);
    List<Post> findTop10ByOrderByViewCountDesc();

}