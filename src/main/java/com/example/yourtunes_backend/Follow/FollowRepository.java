// FollowRepository.java
package com.example.yourtunes_backend.Follow;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findByFollowerUsername(String followerUsername);
    List<Follow> findByFollowingUsername(String followingUsername);

    void deleteByFollowerUsernameAndFollowingUsername(String followerUsername, String followingUsername);  // ✅ 이 줄 추가
}