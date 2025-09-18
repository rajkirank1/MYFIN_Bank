package com.company.userservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.company.userservice.model.dto.CreateUser;
import com.company.userservice.model.dto.UserDto;
import com.company.userservice.model.dto.UserUpdate;
import com.company.userservice.model.dto.UserUpdateStatus;
import com.company.userservice.model.dto.response.Response;
import com.company.userservice.security.UserService;

/**
 * REST controller for user-related operations.
 *
 * Explicit constructor injection is used (no Lombok).
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Explicit constructor injection (no Lombok)
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Create a new user.
     */
    @PostMapping
    public ResponseEntity<Response> createUser(@RequestBody CreateUser createUser) {
        Response resp = userService.createUser(createUser);
        return ResponseEntity.ok(resp);
    }

    /**
     * Partially update an existing user.
     */
    @PutMapping("/{userId}")
    public ResponseEntity<Response> updateUser(
            @PathVariable("userId") Long userId,
            @RequestBody UserUpdate updateDto) {

        Response resp = userService.updateUser(userId, updateDto);
        return ResponseEntity.ok(resp);
    }

    /**
     * Update only the status of a user.
     */
    @PatchMapping("/{userId}/status")
    public ResponseEntity<Response> updateUserStatus(
            @PathVariable("userId") Long userId,
            @RequestBody UserUpdateStatus statusDto) {

        Response resp = userService.updateUserStatus(userId, statusDto);
        return ResponseEntity.ok(resp);
    }

    /**
     * Delete a user.
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Response> deleteUser(@PathVariable("userId") Long userId) {
        Response resp = userService.deleteUser(userId);
        return ResponseEntity.ok(resp);
    }

    /**
     * List all users.
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> listUsers() {
        List<UserDto> users = userService.listUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Read user by ID.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> readUserById(@PathVariable("userId") Long userId) {
        UserDto dto = userService.readUserById(userId);
        return ResponseEntity.ok(dto);
    }

    /**
     * Read user by account ID.
     */
    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<UserDto> readUserByAccountId(@PathVariable("accountId") String accountId) {
        UserDto dto = userService.readUserByAccountId(accountId);
        return ResponseEntity.ok(dto);
    }
}
