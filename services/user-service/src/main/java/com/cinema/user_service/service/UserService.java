package com.cinema.user_service.service;

import com.cinema.user_service.dto.request.CreateUserRequest;
import com.cinema.user_service.dto.request.UpdateUserRequest;
import com.cinema.user_service.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    List<UserResponse> getAllUsers();

    UserResponse getById(Long id);

    UserResponse createUser(CreateUserRequest request);

    UserResponse updateUserById(Long id, UpdateUserRequest request);

    UserResponse findUserByEmail(String email);

    void deleteUserById(Long id);
}