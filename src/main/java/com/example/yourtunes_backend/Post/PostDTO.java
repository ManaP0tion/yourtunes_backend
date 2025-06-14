package com.example.yourtunes_backend.Post;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostDTO {
    private String title;
    private String content;
    private String audioUrl;
    private List<String> imageUrls;

    public PostDTO(Post post) {
        this.title = post.getTitle();
        this.content = post.getContent();
        this.audioUrl = post.getAudioUrl();
        this.imageUrls = post.getImages().stream()
                .map(PostImage::getImageUrl)
                .collect(Collectors.toList());
    }
}