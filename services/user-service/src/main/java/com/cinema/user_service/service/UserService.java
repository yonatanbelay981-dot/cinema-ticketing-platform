package com.cinema.user_service.service;

import com.cinema.user_service.dto.request.CreateUserRequest;
import com.cinema.user_service.dto.request.UpdateUserRequest;
import com.cinema.user_service.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    Page<UserResponse> getAllUsers(
            Pageable pageable
    );

    UserResponse getById(Long id);

    UserResponse createUser(CreateUserRequest request);

    UserResponse updateUserById(Long id, UpdateUserRequest request);

    UserResponse findUserByEmail(String email);

    void deleteUserById(Long id);

    Page<UserResponse> searchByFirstName(
            String firstName,
            Pageable pageable
    );

    Page<UserResponse> searchByLastName(
            String lastName,
            Pageable pageable
    );

    Page<UserResponse> searchByEmail(
            String email,
            Pageable pageable
    );
}