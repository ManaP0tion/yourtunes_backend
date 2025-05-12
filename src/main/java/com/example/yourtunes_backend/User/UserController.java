package com.example.yourtunes_backend.User;

import com.example.yourtunes_backend.Config.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원 가입
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO userDTO) {
        if (userRepository.findByUsername(userDTO.getUsername()) != null) {
            return ResponseEntity.status(409).body("Username already exists");
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
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO) {
        User user = userRepository.findByUsername(loginDTO.getUsername());
        if (user == null || !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        String token = jwtTokenProvider.createToken(user.getUsername());
        return ResponseEntity.ok(token);
    }

    // 유저 단일 조회
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable int userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) return ResponseEntity.status(404).build();

        User user = userOptional.get();
        UserDTO dto = new UserDTO();
        dto.setUsername(user.getUsername());
        dto.setUserEmail(user.getUserEmail());
        dto.setUserBday(user.getUserBday());
        dto.setUserImage(user.getUserImage());
        return ResponseEntity.ok(dto);
    }

    // 전체 유저 조회
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        Iterable<User> users = userRepository.findAll();
        if (!users.iterator().hasNext()) {
            return ResponseEntity.status(404).body("등록된 유저가 없습니다");
        }
        return ResponseEntity.ok(users);
    }

    // 유저 수정
    @PutMapping("/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable int userId, @RequestBody UserDTO dto) {
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

    // 유저 삭제
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable int userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(404).body("User not found");
        }
        userRepository.deleteById(userId);
        return ResponseEntity.ok("User deleted successfully");
    }
}