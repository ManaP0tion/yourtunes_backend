package com.example.yourtunes_backend.Post;

import com.example.yourtunes_backend.Service.FileService;
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


    // CREATE
    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("audio") MultipartFile audio,
            @RequestParam("userId") String userId
    ) throws IOException {
        String audioPath = fileService.save(audio, "audio");
        String imagePath = image != null ? fileService.save(image, "images") : null;

        Post post = new Post(title, content, audioPath, imagePath, userId);
        postRepository.save(post);

        return ResponseEntity.ok(post);
    }

    // READ ALL
    @GetMapping
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // READ ONE
    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id) {
        return postRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // READ BY USER
    @GetMapping("/user/{userId}")
    public List<Post> getPostsByUser(@PathVariable String userId) {
        return postRepository.findByUserId(userId);
    }

    // SEARCH BY TITLE
    @GetMapping("/search")
    public List<Post> searchByTitle(@RequestParam String keyword) {
        return postRepository.findByTitleContainingIgnoreCase(keyword);
    }

    // UPDATE
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

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        if (!postRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        postRepository.deleteById(id);
        return ResponseEntity.ok("Post deleted");
    }
}