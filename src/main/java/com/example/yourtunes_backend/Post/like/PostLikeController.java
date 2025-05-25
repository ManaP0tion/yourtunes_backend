package com.example.yourtunes_backend.Post.like;

import com.example.yourtunes_backend.Post.Post;
import com.example.yourtunes_backend.Post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/likes")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostRepository postRepository;
    private final PostLikeRepository likeRepository;

    @PostMapping
    public ResponseEntity<?> like(@RequestParam String userId, @RequestParam Long postId) {
        if (likeRepository.existsByUserIdAndPost_PostId(userId, postId)) {
            return ResponseEntity.badRequest().body("Already liked");
        }
        Post post = postRepository.findById(postId).orElseThrow();
        PostLike like = new PostLike();
        like.setUserId(userId);
        like.setPost(post);
        likeRepository.save(like);
        return ResponseEntity.ok("Liked");
    }

    @DeleteMapping
    public ResponseEntity<?> unlike(@RequestParam String userId, @RequestParam Long postId) {
        likeRepository.deleteByUserIdAndPost_PostId(userId, postId);
        return ResponseEntity.ok("Unliked");
    }

    @GetMapping("/count/{postId}")
    public long count(@PathVariable Long postId) {
        return likeRepository.countByPost_PostId(postId);
    }
}