package com.example.yourtunes_backend.Post;

import com.example.yourtunes_backend.Comment.Comment;
import com.example.yourtunes_backend.Comment.CommentRepository;
import com.example.yourtunes_backend.Service.FileService;
import com.example.yourtunes_backend.User.User;
import com.example.yourtunes_backend.User.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
    private final PostImageRepository postImageRepository;
    private final FileService fileService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "게시글 생성 (이미지, 오디오 업로드 가능)")
    public ResponseEntity<?> createPost(
            @RequestPart("title")
            @Schema(description = "제목")
            String title,
            @RequestPart("content")
            @Schema(description = "내용")
            String content,
            @RequestPart("username")
            @Schema(description = "작성자 username")
            String username,
            @RequestPart("images")
            List<MultipartFile> images,
            @RequestPart(value = "audio", required = false)
            @Schema(description = "오디오 파일", type = "string", format = "binary")
            MultipartFile audio
    ) throws IOException {
        String audioPath = (audio != null) ? fileService.save(audio, "audio") : null;

        Post post = new Post(title, content, audioPath, username);
        postRepository.save(post);

        if (images != null && images.size() <= 10) {
            for (MultipartFile image : images) {
                String imagePath = fileService.save(image, "images");
                PostImage postImage = new PostImage(imagePath, post);
                post.getImages().add(postImage);
            }
            postRepository.save(post);
        } else if (images != null) {
            return ResponseEntity.badRequest().body("이미지는 최대 10장까지 업로드 가능합니다.");
        }

        return ResponseEntity.ok(post);
    }

    @GetMapping
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        return postRepository.findById(id).map(post -> {
            post.setViewCount(post.getViewCount() + 1);
            postRepository.save(post);
            return ResponseEntity.ok(post);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<PostDTO>> getPostsByUsername(@PathVariable String username) {
        List<Post> posts = postRepository.findByUserId(username); // username 직접 사용!
        List<PostDTO> postDTOs = posts.stream()
                .map(PostDTO::new)
                .toList();
        return ResponseEntity.ok(postDTOs);
    }

    @GetMapping("/search")
    public List<Post> searchByTitle(@RequestParam String keyword) {
        return postRepository.findByTitleContainingIgnoreCase(keyword);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long id,
            @RequestPart("title") String title,
            @RequestPart("content") String content,
            @RequestPart(value = "images", required = false) List<MultipartFile> newImages
    ) throws IOException {
        return postRepository.findById(id).map(post -> {
            post.setTitle(title);
            post.setContent(content);

            post.getImages().clear();

            if (newImages != null && newImages.size() <= 10) {
                for (MultipartFile image : newImages) {
                    try {
                        String imagePath = fileService.save(image, "images");
                        PostImage postImage = new PostImage(imagePath, post);
                        post.getImages().add(postImage);
                    } catch (IOException e) {
                        return ResponseEntity.status(500).body("이미지 업로드 실패");
                    }
                }
            } else if (newImages != null) {
                return ResponseEntity.badRequest().body("이미지는 최대 10장까지 업로드 가능합니다.");
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
        return ResponseEntity.ok(commentRepository.findByPostPostId(postId));
    }

    @GetMapping("/top10")
    public ResponseEntity<List<PostDTO>> getTop10Posts() {
        List<Post> topPosts = postRepository.findTop10ByOrderByViewCountDesc();
        List<PostDTO> result = topPosts.stream().map(PostDTO::new).toList();
        return ResponseEntity.ok(result);
    }
}