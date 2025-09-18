package com.company.userservice.model.mapper;

import org.springframework.stereotype.Component;

import com.company.userservice.model.dto.CreateUser;
import com.company.userservice.model.dto.UserDto;
import com.company.userservice.model.dto.UserUpdate;
import com.company.userservice.model.entity.User;

@Component
public class UserMapper {

    public User toEntity(CreateUser dto) {
        if (dto == null) return null;
        User u = new User();
        u.setEmail(dto.getEmail());
        u.setFirstName(dto.getFirstName());
        u.setLastName(dto.getLastName());
        u.setPassword(dto.getPassword());
        // CreateUser may not have role field; default to "USER"
        u.setRole(dto instanceof CreateUser && dto.getClass().getDeclaredMethods() != null ? null : "USER");
        // set explicit default status if your User.Status exists
        try {
            u.setStatus(User.Status.PENDING);
        } catch (Exception ignored) {
            // If enum or method differs, remove or adapt this line
        }
        return u;
    }

    public UserDto toDto(User user) {
        if (user == null) return null;
        UserDto d = new UserDto();
        d.setId(user.getId());
        d.setAuthId(user.getAuthId());
        d.setEmail(user.getEmail());
        d.setFirstName(user.getFirstName());
        d.setLastName(user.getLastName());
        d.setRole(user.getRole());
        d.setStatus(user.getStatus() != null ? user.getStatus().name() : null);
        d.setCreatedAt(user.getCreatedAt());
        d.setUpdatedAt(user.getUpdatedAt());
        return d;
    }

    public void applyUpdateToEntity(UserUpdate update, User entity) {
        if (update == null || entity == null) return;
        if (update.getFirstName() != null) entity.setFirstName(update.getFirstName());
        if (update.getLastName() != null) entity.setLastName(update.getLastName());
        if (update.getPhone() != null) {
            // adapt to your entity field if present
        }
    }
}
