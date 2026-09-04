package com.cinema.user_service.controller;

import com.cinema.user_service.dto.common.ApiResponse;
import com.cinema.user_service.dto.request.CreateUserRequest;
import com.cinema.user_service.dto.request.UpdateUserRequest;
import com.cinema.user_service.dto.response.UserResponse;
import com.cinema.user_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public  ResponseEntity<ApiResponse<UserResponse>>getMe(
            @AuthenticationPrincipal Jwt jwt
            ){
        String keyClockId  = jwt.getSubject();
        UserResponse user = userService.getByKeycloakId(keyClockId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true ,
                        "user fetched successfully",
                        user

                )
        );

    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(Pageable pageable){
        Page<UserResponse>  users = userService.getAllUsers(pageable);
        ApiResponse<Page<UserResponse>> response =  new ApiResponse<>(true ,   "Users fetched successfully" , users);
        return ResponseEntity.ok(response);


    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable UUID id
    ){
        UserResponse user = userService.getById(id);
        ApiResponse<UserResponse> response = new ApiResponse<>(true , "User retrieved successfully" ,  user);

         return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/search")
    public  ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(
            @RequestParam String email
    ){
      UserResponse user = userService.findUserByEmail(email);
      ApiResponse<UserResponse> response = new ApiResponse<>(true ,  "User found successfully" , user);
        return ResponseEntity.ok(
                response
        );
    }


    @PostMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateUserRequest request
    ){
        String keycloakUserId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        UserResponse user = userService.createUser(keycloakUserId , email ,request);
        ApiResponse<UserResponse> response = new ApiResponse<>(true   , "user created successfully"  , user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request
    ){

        UserResponse user = userService.updateUserById(id, request);
        ApiResponse<UserResponse> response = new ApiResponse<>(true   , "user updated successfully"  , user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserProfile(
           @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateUserRequest request
    ){

        String keycloakUserId = jwt.getSubject();
        UserResponse user = userService.updateMyProfile(keycloakUserId, request);
        ApiResponse<UserResponse> response = new ApiResponse<>(true   , "user updated successfully"  , user);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUserById(
            @PathVariable UUID id) {

        userService.deleteUserById(id);

        ApiResponse<Void> response =
                new ApiResponse<>(true, "User deleted successfully", null);

        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/search/firstName")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> searchByFirstName(@RequestParam String firstName  , @PageableDefault(size = 10  , page = 0 ,sort = "createdAt" , direction =  Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.ok(
                new ApiResponse<>(true ,
                                  "user was found successfully by firstname",
                                           userService.searchByFirstName(firstName , pageable) )
        );

    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/search/lastName")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> searchByLastName(@RequestParam String lastName  ,  @PageableDefault(size = 10  , page = 0 , sort = "createdAt" ,  direction = Sort.Direction.DESC)  Pageable pageable){
        return ResponseEntity.ok(
                new ApiResponse<>(true ,
                        "user was found successfully by lastname",
                        userService.searchByLastName(lastName , pageable) )
        );

    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/search/email")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> searchByEmail(@RequestParam String email  ,  @PageableDefault(size = 10  , page = 0 , sort = "createdAt" , direction = Sort.Direction.DESC)  Pageable pageable){
        return ResponseEntity.ok(
                new ApiResponse<>(true ,
                        "user was found successfully by email",
                        userService.searchByEmail(email , pageable) )
        );

    }

}