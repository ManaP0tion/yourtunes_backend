package com.example.yourtunes_backend.User;

import com.example.yourtunes_backend.Config.JwtTokenProvider;
import com.example.yourtunes_backend.Follow.FollowRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final FollowRepository followRepository;

    // 회원가입 (multipart/form-data 지원)
    @PostMapping(value = "/register", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String userEmail,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate userBday,
            @RequestPart(value = "userImage", required = false) MultipartFile userImageFile
    ) {
        if (userRepository.findByUsername(username) != null || userRepository.findByUserEmail(userEmail) != null) {
            return ResponseEntity.status(409).body("Username or Email already exists");
        }

        String imagePath = null;
        if (userImageFile != null && !userImageFile.isEmpty()) {
            String fileName = java.util.UUID.randomUUID() + "_" + userImageFile.getOriginalFilename();
            java.nio.file.Path path = java.nio.file.Paths.get("uploads", fileName);
            try {
                java.nio.file.Files.createDirectories(path.getParent());
                java.nio.file.Files.write(path, userImageFile.getBytes());
                imagePath = "/uploads/" + fileName;
            } catch (java.io.IOException e) {
                return ResponseEntity.status(500).body("Failed to save image");
            }
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setUserEmail(userEmail);
        user.setUserBday(userBday);
        user.setUserImage(imagePath);
        user.setUserCreate(LocalDate.now());

        userRepository.save(user);
        String token = jwtTokenProvider.createToken(user.getUsername());
        return ResponseEntity.ok(token);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDTO loginDTO) {
        User user = userRepository.findByUsername(loginDTO.getUsername());
        if (user == null || !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String token = jwtTokenProvider.createToken(user.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getUserId());
        response.put("username", user.getUsername());
        response.put("userImage", user.getUserImage());

        return ResponseEntity.ok(response);
    }

    /* 유저 단일 조회
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable int userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) return ResponseEntity.status(404).build();

        User user = optionalUser.get();
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setUserEmail(user.getUserEmail());
        dto.setUserBday(user.getUserBday());
        dto.setUserImage(user.getUserImage());
        dto.setUserCreate(user.getUserCreate());

        return ResponseEntity.ok(dto);
    }
    */

    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) return ResponseEntity.status(404).build();

        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setUserEmail(user.getUserEmail());
        dto.setUserBday(user.getUserBday());
        dto.setUserImage(user.getUserImage());
        dto.setUserCreate(user.getUserCreate());

        return ResponseEntity.ok(dto);
    }

    // 전체 유저 조회
    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> result = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            UserResponseDTO dto = new UserResponseDTO();
            dto.setUserId(user.getUserId());
            dto.setUsername(user.getUsername());
            dto.setUserEmail(user.getUserEmail());
            dto.setUserBday(user.getUserBday());
            dto.setUserImage(user.getUserImage());
            dto.setUserCreate(user.getUserCreate());
            result.add(dto);
        }

        return ResponseEntity.ok(result);
    }

    /* 유저 정보 수정
    @PutMapping("/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable int userId, @RequestBody UserRequestDTO dto) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) return ResponseEntity.status(404).body("User not found");

        User user = optionalUser.get();
        if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getUserEmail() != null) user.setUserEmail(dto.getUserEmail());
        if (dto.getUserImage() != null) user.setUserImage(dto.getUserImage());
        if (dto.getPassword() != null) user.setPassword(passwordEncoder.encode(dto.getPassword()));
        if (dto.getUserBday() != null) user.setUserBday(dto.getUserBday());

        userRepository.save(user);
        return ResponseEntity.ok("User updated successfully");
    }
    */

    // 유저정보 수정
    @PutMapping("/username/{username}")
    public ResponseEntity<String> updateUserByUsername(@PathVariable String username, @RequestBody UserRequestDTO dto) {
        User user = userRepository.findByUsername(username);
        if (user == null) return ResponseEntity.status(404).body("User not found");

        if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getUserEmail() != null) user.setUserEmail(dto.getUserEmail());
        if (dto.getUserImage() != null) user.setUserImage(dto.getUserImage());
        if (dto.getPassword() != null) user.setPassword(passwordEncoder.encode(dto.getPassword()));
        if (dto.getUserBday() != null) user.setUserBday(dto.getUserBday());

        userRepository.save(user);
        return ResponseEntity.ok("User updated successfully");
    }

    // 유저삭제
    @DeleteMapping("/username/{username}")
    public ResponseEntity<String> deleteUserByUsername(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) return ResponseEntity.status(404).body("User not found");

        followRepository.deleteAll(followRepository.findByFollowerUsername(username));
        followRepository.deleteAll(followRepository.findByFollowingUsername(username));
        userRepository.delete(user);
        return ResponseEntity.ok("User deleted successfully");
    }


}
