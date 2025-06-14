package com.example.yourtunes_backend.Follow;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "follow")
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String followerUsername; // 팔로우 요청을 보낸 사용자

    @Column(nullable = false)
    private String followingUsername; // 팔로우 당한 사용자
}
