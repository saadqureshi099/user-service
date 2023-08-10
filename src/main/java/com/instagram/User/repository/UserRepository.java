package com.instagram.User.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.instagram.User.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
}
