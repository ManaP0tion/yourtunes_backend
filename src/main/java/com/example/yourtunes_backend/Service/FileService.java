package com.example.yourtunes_backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileService {

    private static final String BASE_DIR = "uploads"; // 프로젝트 내 uploads 폴더

    public String save(MultipartFile file, String folder) throws IOException {
        // 파일명 유니크하게 생성
        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        String newFilename = UUID.randomUUID() + extension;

        // 저장할 경로 설정
        Path directory = Paths.get(BASE_DIR, folder);
        Files.createDirectories(directory);

        Path savePath = directory.resolve(newFilename);

        // 파일 저장
        Files.copy(file.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);

        // 클라이언트가 접근 가능한 경로로 반환
        return "/" + BASE_DIR + "/" + folder + "/" + newFilename;
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex != -1 ? filename.substring(dotIndex) : "";
    }
}