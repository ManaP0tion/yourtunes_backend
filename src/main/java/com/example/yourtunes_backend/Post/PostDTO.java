package com.example.yourtunes_backend.Post;

import lombok.Getter;

import java.util.List;

@Getter
public class PostDTO {
    private Long postId;
    private String title;
    private String content;
    private String audioUrl;
    private List<String> imageUrls;
    private int viewCount;

    public PostDTO(Post post) {
        this.postId = post.getPostId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.audioUrl = post.getAudioUrl();
        this.viewCount = post.getViewCount();
        this.imageUrls = post.getImages().stream()
                .map(PostImage::getImageUrl)
                .toList();
    }
}