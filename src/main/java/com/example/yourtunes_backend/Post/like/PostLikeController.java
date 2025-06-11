package com.example.yourtunes_backend.Post.like;
import com.example.yourtunes_backend.Post.like.PostLike;
import com.example.yourtunes_backend.Post.like.PostLikeRepository;
import com.example.yourtunes_backend.User.User;
import com.example.yourtunes_backend.User.UserRepository;


import com.example.yourtunes_backend.Post.Post;
import com.example.yourtunes_backend.Post.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/likes")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostRepository postRepository;
    private final PostLikeRepository likeRepository;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> like(@RequestParam String username, @RequestParam Long postId) {
        User user = userRepository.findByUsername(username);
        if (user == null) return ResponseEntity.badRequest().body("User not found");

        if (likeRepository.existsByUserIdAndPost_PostId(username, postId)) {
            return ResponseEntity.badRequest().body("Already liked");
        }
        Post post = postRepository.findById(postId).orElseThrow();
        PostLike like = new PostLike();
        like.setUserId(username);
        like.setPost(post);
        likeRepository.save(like);
        return ResponseEntity.ok("Liked");
    }

    @Transactional
    @DeleteMapping
    public ResponseEntity<?> unlike(@RequestParam String username, @RequestParam Long postId) {
        likeRepository.deleteByUserIdAndPost_PostId(username, postId);
        return ResponseEntity.ok("Unliked");
    }

    @GetMapping("/count/{postId}")
    public long count(@PathVariable Long postId) {
        return likeRepository.countByPost_PostId(postId);
    }
}