package com.example.yourtunes_backend.Post;

import com.example.yourtunes_backend.Comment.Comment;
import com.example.yourtunes_backend.Service.FileService;
import com.example.yourtunes_backend.User.User;
import com.example.yourtunes_backend.User.UserRepository;
import com.example.yourtunes_backend.Comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostRepository postRepository;
    private final FileService fileService;
    private final UserRepository userRepository;
    private final CommentRepository CommentRepository;

    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "audio", required = false) MultipartFile audio,
            @RequestParam("username") String username
    ) throws IOException {
        String audioPath = audio != null ? fileService.save(audio, "audio") : null;
        String imagePath = image != null ? fileService.save(image, "images") : null;

        Post post = new Post(title, content, audioPath, imagePath, username);
        postRepository.save(post);

        return ResponseEntity.ok(post);
    }

    @GetMapping
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id) {
        return postRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<Post>> getPostsByUsername(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        List<Post> posts = postRepository.findByUserId(String.valueOf(user.getUserId()));
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/search")
    public List<Post> searchByTitle(@RequestParam String keyword) {
        return postRepository.findByTitleContainingIgnoreCase(keyword);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) throws IOException {
        return postRepository.findById(id).map(post -> {
            post.setTitle(title);
            post.setContent(content);
            if (image != null) {
                try {
                    String newImagePath = fileService.save(image, "images");
                    post.setImageUrl(newImagePath);
                } catch (IOException e) {
                    return ResponseEntity.status(500).body("Image upload failed");
                }
            }
            postRepository.save(post);
            return ResponseEntity.ok(post);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        if (!postRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        postRepository.deleteById(id);
        return ResponseEntity.ok("Post deleted");
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<Comment>> getCommentsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(CommentRepository.findByPostPostId(postId));
    }
}