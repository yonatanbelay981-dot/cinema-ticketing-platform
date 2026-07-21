package com.cinema.user_service.controller;

import com.cinema.user_service.dto.common.ApiResponse;
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
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(){
        List<UserResponse>  users = userService.getAllUsers();
        ApiResponse<List<UserResponse>> response =  new ApiResponse<>(true ,   "Users fetched successfully" , users);
        return ResponseEntity.ok(response);


    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable Long id
    ){
        UserResponse user = userService.getById(id);
        ApiResponse<UserResponse> response = new ApiResponse<>(true , "User retrieved successfully" ,  user);

         return ResponseEntity.ok(response);
    }


    @GetMapping("/search")
    public  ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(
            @RequestParam String email
    ){
      UserResponse user = userService.findUserByEmail(email);
      ApiResponse<UserResponse> response = new ApiResponse<>(true ,  "User found successfully" , user);
        return ResponseEntity.ok(
                response
        );
    }


    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request
    ){

        UserResponse user = userService.createUser(request);
        ApiResponse<UserResponse> response = new ApiResponse<>(true   , "user created successfully"  , user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request
    ){

        UserResponse user = userService.updateUserById(id, request);
        ApiResponse<UserResponse> response = new ApiResponse<>(true   , "user updated successfully"  , user);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUserById(
            @PathVariable Long id) {

        userService.deleteUserById(id);

        ApiResponse<Void> response =
                new ApiResponse<>(true, "User deleted successfully", null);

        return ResponseEntity.ok(response);
    }

}