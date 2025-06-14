package com.example.yourtunes_backend.User;

import com.example.yourtunes_backend.Config.JwtTokenProvider;
import com.example.yourtunes_backend.Follow.FollowRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid UserRequestDTO userDTO) {
        if (userRepository.findByUsername(userDTO.getUsername()) != null ||
                userRepository.findByUserEmail(userDTO.getUserEmail()) != null) {
            return ResponseEntity.status(409).body("Username or Email already exists");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setUserEmail(userDTO.getUserEmail());
        user.setUserBday(userDTO.getUserBday());
        user.setUserImage(userDTO.getUserImage());
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
