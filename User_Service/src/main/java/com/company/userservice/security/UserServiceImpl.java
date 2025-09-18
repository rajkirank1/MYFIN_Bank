package com.company.userservice.security;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.company.userservice.model.dto.CreateUser;
import com.company.userservice.model.dto.UserDto;
import com.company.userservice.model.dto.UserUpdate;
import com.company.userservice.model.dto.UserUpdateStatus;
import com.company.userservice.model.dto.response.Response;
import com.company.userservice.model.entity.User;
import com.company.userservice.model.mapper.UserMapper;

/**
 * In-memory implementation of UserService (and UserDetails lookup).
 *
 * Note: ensure your UserService interface extends
 * org.springframework.security.core.userdetails.UserDetailsService
 * (so this class must implement loadUserByUsername).
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserMapper userMapper;

    // in-memory storage
    private final Map<Long, User> store = new ConcurrentHashMap<>();
    private final Map<String, Long> accountIndex = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    // configurable response codes/messages (defaults)
    private String responseCodeSuccess = "00";
    private String responseMessageSuccess = "SUCCESS";
    private String responseCodeNotFound = "404";
    private String responseMessageNotFound = "NOT_FOUND";

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = Objects.requireNonNull(userMapper, "userMapper must not be null");
    }

    @Override
    public Response createUser(CreateUser createDto) {
        Response resp = new Response();
        if (createDto == null) {
            resp.setResponseCode("ERR");
            resp.setResponseMessage("create payload is null");
            return resp;
        }

        // map DTO -> entity
        User entity = userMapper.toEntity(createDto);
        if (entity == null) {
            resp.setResponseCode("ERR");
            resp.setResponseMessage("failed to map create payload");
            return resp;
        }

        long id = idGenerator.getAndIncrement();
        entity.setId(id);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        // create an accountId (simple UUID string) and keep index
        String accountId = UUID.randomUUID().toString();
        accountIndex.put(accountId, id);

        // store entity
        store.put(id, entity);

        resp.setResponseCode(responseCodeSuccess);
        resp.setResponseMessage(responseMessageSuccess + " - created id=" + id + " accountId=" + accountId);
        return resp;
    }

    @Override
    public Response updateUser(Long userId, UserUpdate updateDto) {
        Response resp = new Response();
        if (userId == null || updateDto == null) {
            resp.setResponseCode("ERR");
            resp.setResponseMessage("invalid arguments");
            return resp;
        }

        User existing = store.get(userId);
        if (existing == null) {
            resp.setResponseCode(responseCodeNotFound);
            resp.setResponseMessage(responseMessageNotFound);
            return resp;
        }

        // apply partial update using mapper
        userMapper.applyUpdateToEntity(updateDto, existing);
        existing.setUpdatedAt(Instant.now());
        store.put(userId, existing);

        resp.setResponseCode(responseCodeSuccess);
        resp.setResponseMessage(responseMessageSuccess + " - updated id=" + userId);
        return resp;
    }

    @Override
    public Response updateUserStatus(Long userId, UserUpdateStatus statusDto) {
        Response resp = new Response();
        if (userId == null || statusDto == null || statusDto.getStatus() == null) {
            resp.setResponseCode("ERR");
            resp.setResponseMessage("invalid arguments");
            return resp;
        }

        User existing = store.get(userId);
        if (existing == null) {
            resp.setResponseCode(responseCodeNotFound);
            resp.setResponseMessage(responseMessageNotFound);
            return resp;
        }

        try {
            // Convert the String from DTO -> Enum (allow case-insensitive input)
            User.Status newStatus = User.Status.valueOf(statusDto.getStatus().trim().toUpperCase());
            existing.setStatus(newStatus);
            existing.setUpdatedAt(Instant.now());
            store.put(userId, existing);

            resp.setResponseCode(responseCodeSuccess);
            resp.setResponseMessage(responseMessageSuccess + " - status updated for id=" + userId);
        } catch (IllegalArgumentException e) {
            resp.setResponseCode("ERR");
            resp.setResponseMessage("Invalid status value: " + statusDto.getStatus());
        }

        return resp;
    }

    @Override
    public Response deleteUser(Long userId) {
        Response resp = new Response();
        if (userId == null) {
            resp.setResponseCode("ERR");
            resp.setResponseMessage("invalid user id");
            return resp;
        }

        User removed = store.remove(userId);
        if (removed == null) {
            resp.setResponseCode(responseCodeNotFound);
            resp.setResponseMessage(responseMessageNotFound);
            return resp;
        }

        // remove any account index entries referencing this user (thread-safe)
        List<String> keysToRemove = accountIndex.entrySet().stream()
                .filter(e -> Objects.equals(e.getValue(), userId))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        for (String k : keysToRemove) {
            accountIndex.remove(k);
        }

        resp.setResponseCode(responseCodeSuccess);
        resp.setResponseMessage(responseMessageSuccess + " - deleted id=" + userId);
        return resp;
    }

    @Override
    public List<UserDto> listUsers() {
        return store.values().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto readUserById(Long userId) {
        User u = store.get(userId);
        return u == null ? null : userMapper.toDto(u);
    }

    @Override
    public UserDto readUserByAccountId(String accountId) {
        if (accountId == null) return null;
        Long id = accountIndex.get(accountId);
        if (id == null) return null;
        return readUserById(id);
    }

    /**
     * Required by UserDetailsService (UserService should extend that).
     * Looks up by email first, then authId.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.isBlank()) {
            throw new UsernameNotFoundException("username is null/blank");
        }

        // Lookup by email or authId (adjust to your actual username field)
        User found = store.values().stream()
                .filter(u -> (u.getEmail() != null && u.getEmail().equalsIgnoreCase(username))
                          || (u.getAuthId() != null && u.getAuthId().equalsIgnoreCase(username)))
                .findFirst()
                .orElse(null);

        if (found == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // Build authorities from stored role. If your role values don't include "ROLE_" prefix,
        // add it. Adjust as needed.
        String role = found.getRole();
        String authority = (role == null || role.isBlank()) ? "ROLE_USER"
                : (role.startsWith("ROLE_") ? role : "ROLE_" + role);

        // Use Spring Security's User builder (use fully-qualified name to avoid confusion with entity)
        return org.springframework.security.core.userdetails.User.builder()
                .username(found.getEmail() != null ? found.getEmail() : (found.getAuthId() != null ? found.getAuthId() : ""))
                .password(found.getPassword() != null ? found.getPassword() : "") // must not be null
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(authority)))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    // getters / setters for response code/messages (explicit)
    @Override
    public String getResponseCodeSuccess() {
        return this.responseCodeSuccess;
    }

    @Override
    public void setResponseCodeSuccess(String responseCodeSuccess) {
        this.responseCodeSuccess = responseCodeSuccess;
    }

    @Override
    public String getResponseMessageSuccess() {
        return this.responseMessageSuccess;
    }

    @Override
    public void setResponseMessageSuccess(String responseMessageSuccess) {
        this.responseMessageSuccess = responseMessageSuccess;
    }

    @Override
    public String getResponseCodeNotFound() {
        return this.responseCodeNotFound;
    }

    @Override
    public void setResponseCodeNotFound(String responseCodeNotFound) {
        this.responseCodeNotFound = responseCodeNotFound;
    }

    @Override
    public String getResponseMessageNotFound() {
        return this.responseMessageNotFound;
    }

    @Override
    public void setResponseMessageNotFound(String responseMessageNotFound) {
        this.responseMessageNotFound = responseMessageNotFound;
    }
}
