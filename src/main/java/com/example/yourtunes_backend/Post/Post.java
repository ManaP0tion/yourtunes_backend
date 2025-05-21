package com.example.yourtunes_backend.Post;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;

    private String audioUrl;
    private String imageUrl;

    private String userId;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Post(String title, String content, String audioUrl, String imageUrl, String userId) {
        this.title = title;
        this.content = content;
        this.audioUrl = audioUrl;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.createdAt = LocalDateTime.now(); // 기본값 지정
    }
}