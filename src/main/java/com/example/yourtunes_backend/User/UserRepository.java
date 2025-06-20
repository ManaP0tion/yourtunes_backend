package com.example.yourtunes_backend.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer>{
    User findByUsername(String username);
    User findByUserEmail(String userEmail);
}
