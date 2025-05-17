package com.example.yourtunes_backend.User;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserResponseDTO {
    private int userId;
    private String username;
    private String userEmail;
    private LocalDate userBday;
    private String userImage;
    private LocalDate userCreate;
}
