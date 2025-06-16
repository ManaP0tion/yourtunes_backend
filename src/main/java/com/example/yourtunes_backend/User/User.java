package com.example.yourtunes_backend.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "\"user\"")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(length = 20, nullable = false)
    private String username;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(length = 20)
    private String userEmail;

    @Column
    private LocalDate userBday;

    @Column(length = 256)
    private String userImage;

    @Column
    private LocalDate userCreate;
}