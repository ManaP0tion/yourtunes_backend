package com.example.yourtunes_backend.Post;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    private String title;
    private String content;

    private String audioUrl;

    private String userId;

    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(nullable = false)
    private int viewCount = 0;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PostImage> images = new ArrayList<>();

    public Post(String title, String content, String audioUrl, String userId) {
        this.title = title;
        this.content = content;
        this.audioUrl = audioUrl;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
    }
}