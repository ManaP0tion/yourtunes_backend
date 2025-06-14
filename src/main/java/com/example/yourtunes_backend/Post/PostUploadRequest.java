package com.example.yourtunes_backend.Post;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Schema(name = "PostUploadRequest", description = "게시글 업로드 요청 스키마")
public class PostUploadRequest {

    @Schema(description = "제목", example = "내 첫 번째 게시글")
    public String title;

    @Schema(description = "내용", example = "안녕하세요, 첫 게시글입니다.")
    public String content;

    @Schema(description = "작성자", example = "testuser")
    public String username;

    @Schema(description = "오디오 파일", type = "string", format = "binary")
    public MultipartFile audio;

    @Schema(description = "이미지 파일 리스트 (최대 10장)", type = "array", format = "binary")
    public List<MultipartFile> images;
}