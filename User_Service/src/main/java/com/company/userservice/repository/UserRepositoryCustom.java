package com.company.userservice.repository;

import com.company.userservice.model.entity.User;

import java.util.Optional;

public interface UserRepositoryCustom {
    Optional<User> findByAuthIdCustom(String authId);
}