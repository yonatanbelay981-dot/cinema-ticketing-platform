package com.cinema.user_service.controller;

import com.cinema.user_service.dto.request.CreateUserRequest;
import com.cinema.user_service.dto.request.UpdateUserRequest;
import com.cinema.user_service.dto.response.UserResponse;
import com.cinema.user_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(){

        return ResponseEntity.ok(
                userService.getAllUsers()
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id
    ){

        return ResponseEntity.ok(
                userService.getById(id)
        );
    }


    @GetMapping("/search")
    public ResponseEntity<UserResponse> getUserByEmail(
            @RequestParam String email
    ){

        return ResponseEntity.ok(
                userService.findUserByEmail(email)
        );
    }


    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request
    ){

        UserResponse response = userService.createUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request
    ){

        UserResponse response = userService.updateUserById(id, request);

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(
            @PathVariable Long id
    ){

        userService.deleteUserById(id);

        return ResponseEntity
                .noContent()
                .build();
    }

}