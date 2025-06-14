package com.example.yourtunes_backend.Follow;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowRepository followRepository;

    @PostMapping("/{follower}/{following}")
    public ResponseEntity<String> follow(@PathVariable String follower, @PathVariable String following) {
        if (follower.equals(following)) return ResponseEntity.badRequest().body("자기 자신은 팔로우할 수 없습니다");

        boolean exists = followRepository.findByFollowerUsername(follower).stream()
                .anyMatch(f -> f.getFollowingUsername().equals(following));
        if (exists) return ResponseEntity.badRequest().body("이미 팔로우 중입니다");

        Follow follow = new Follow();
        follow.setFollowerUsername(follower);
        follow.setFollowingUsername(following);
        followRepository.save(follow);
        return ResponseEntity.ok("팔로우 완료");
    }

    @DeleteMapping("/{follower}/{following}")
    public ResponseEntity<String> unfollow(@PathVariable String follower, @PathVariable String following) {
        followRepository.deleteByFollowerUsernameAndFollowingUsername(follower, following);
        return ResponseEntity.ok("언팔로우 완료");
    }

    @GetMapping("/followers/{username}")
    public List<String> getFollowers(@PathVariable String username) {
        return followRepository.findByFollowingUsername(username).stream()
                .map(Follow::getFollowerUsername)
                .collect(Collectors.toList());
    }

    @GetMapping("/followings/{username}")
    public List<String> getFollowings(@PathVariable String username) {
        return followRepository.findByFollowerUsername(username).stream()
                .map(Follow::getFollowingUsername)
                .collect(Collectors.toList());
    }

    @GetMapping("/count/{username}")
    public Map<String, Long> getFollowCounts(@PathVariable String username) {
        long followers = followRepository.findByFollowingUsername(username).size();
        long followings = followRepository.findByFollowerUsername(username).size();
        return Map.of("followers", followers, "followings", followings);
    }
}
