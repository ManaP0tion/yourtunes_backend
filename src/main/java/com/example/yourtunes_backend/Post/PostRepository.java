package com.example.yourtunes_backend.Post;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 사용자 ID 기준으로 포스트 찾기
    List<Post> findByUserId(String userId);

    // 제목 키워드 검색 (옵션)
    List<Post> findByTitleContainingIgnoreCase(String keyword);
}