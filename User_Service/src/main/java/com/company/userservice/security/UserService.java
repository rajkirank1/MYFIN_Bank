package com.company.userservice.security;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.company.userservice.model.dto.CreateUser;
import com.company.userservice.model.dto.UserDto;
import com.company.userservice.model.dto.UserUpdate;
import com.company.userservice.model.dto.UserUpdateStatus;
import com.company.userservice.model.dto.response.Response;

/**
 * Service interface for user operations.
 * Extends Spring Security's UserDetailsService so it can be used by
 * DaoAuthenticationProvider and JwtFilter.
 */
public interface UserService extends UserDetailsService {

    Response createUser(CreateUser createDto);

    Response updateUser(Long userId, UserUpdate updateDto);

    Response updateUserStatus(Long userId, UserUpdateStatus statusDto);

    Response deleteUser(Long userId);

    List<UserDto> listUsers();

    UserDto readUserById(Long userId);

    UserDto readUserByAccountId(String accountId);

    String getResponseCodeSuccess();
    void setResponseCodeSuccess(String responseCodeSuccess);

    String getResponseMessageSuccess();
    void setResponseMessageSuccess(String responseMessageSuccess);

    String getResponseCodeNotFound();
    void setResponseCodeNotFound(String responseCodeNotFound);

    String getResponseMessageNotFound();
    void setResponseMessageNotFound(String responseMessageNotFound);
}
