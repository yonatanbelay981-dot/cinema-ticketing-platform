package com.cinema.user_service.service;

import com.cinema.user_service.dto.request.CreateUserRequest;
import com.cinema.user_service.dto.request.UpdateUserRequest;
import com.cinema.user_service.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface UserService {

    Page<UserResponse> getAllUsers(
            Pageable pageable
    );

    UserResponse getById(UUID id);

    UserResponse createUser(String keyClockId ,  String email ,  CreateUserRequest request);

    UserResponse updateUserById(UUID id, UpdateUserRequest request);

    UserResponse findUserByEmail(String email);

    void deleteUserById(UUID id);

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
    UserResponse getByKeycloakId(String  keycloakUserId);
    UserResponse updateMyProfile(String keycloakUserId, UpdateUserRequest request);
}