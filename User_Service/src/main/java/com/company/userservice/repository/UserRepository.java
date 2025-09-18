package com.company.userservice.repository;

import com.company.userservice.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByAuthId(String authId);
    Optional<User> findByEmail(String email);
}
