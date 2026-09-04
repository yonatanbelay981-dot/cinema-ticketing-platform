package com.cinema.user_service.service;

import com.cinema.user_service.dto.request.CreateUserRequest;
import com.cinema.user_service.dto.request.UpdateUserRequest;
import com.cinema.user_service.dto.response.UserResponse;
import com.cinema.user_service.entity.User;
import com.cinema.user_service.exception.UserNotFoundException;
import com.cinema.user_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Service
@Slf4j
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;

    public UserServiceImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.info("Fetching all users");

         return userRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public UserResponse getById(UUID id) {
        log.info("Fetching user with id {}"  , id);
        User user = userRepository.findById(id).orElseThrow(()->{ log.warn("User with id {} not found", id);
            return new UserNotFoundException("user  not found with id " +  id);});
        return mapToResponse(user);
    }

    @Override
    public UserResponse createUser(String keycloakUserId , String email ,  CreateUserRequest request) {
        log.info("Creating user with email {}"  , email);
        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setKeycloakUserId(keycloakUserId);
        user.setLastName(request.getLastName());
        user.setEmail(email);
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDateOfBirth(request.getDateOfBirth());


        User savedUser = userRepository.save(user);
        log.info("user successfully created with id {} "  , savedUser.getId());


        return mapToResponse(savedUser);
    }
    @Override
    public UserResponse getByKeycloakId(String keycloakUserId) {

        User user = userRepository
                .findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User profile not found"
                        )
                );

        return mapToResponse(user);
    }

    @Override
    public UserResponse updateMyProfile(String keycloakUserId, UpdateUserRequest request) {
        log.info("Updating user with keycloakUserId {}", keycloakUserId);
        User user = userRepository.findByKeycloakUserId(keycloakUserId).orElseThrow(()->{ log.warn("User not found with keycloakUserId  {}", keycloakUserId);
            return new UserNotFoundException("user not found with id : " + keycloakUserId);});

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setProfileImage(request.getProfileImage());

        User updatedUser = userRepository.save(user);
        log.info("user updated successfully with keycloakUserId {}" ,  keycloakUserId);
        return mapToResponse(updatedUser) ;

    }

    @Override
    public UserResponse updateUserById(UUID id, UpdateUserRequest request) {
        log.info("Updating user with id {}", id);
        User user = userRepository.findById(id).orElseThrow(()->{ log.warn("User not found with id  {}", id);
            return new UserNotFoundException("user not found with id : " + id);});

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setProfileImage(request.getProfileImage());


        User updatedUser = userRepository.save(user);
        log.info("user updated successfully with id {}" ,  updatedUser.getId());
        return mapToResponse(updatedUser) ;

    }

    @Override
    public UserResponse findUserByEmail(String email) {
        log.info("Searching user by email {}", email);
        User user  =  userRepository.findByEmail(email).orElseThrow(
                ()->{
                    log.warn("User with email {} not found", email);
                    return new UserNotFoundException("user not found with email" +  email);});

        return mapToResponse(user);
    }

    @Override
    public void deleteUserById(UUID id) {
        log.info("Deleting user with id {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User {} not found", id);
                    return new UserNotFoundException(
                            "User not found"
                    );
                });
        userRepository.delete(user);
        log.info("User {} deleted successfully", id);

    }
    @Override
    public Page<UserResponse> searchByFirstName(
            String firstName,
            Pageable pageable
    ) {
        log.info("Searching users by first name: {}", firstName);

        Page<UserResponse> users =  userRepository
                .findByFirstNameContainingIgnoreCase(
                        firstName,
                        pageable
                )
                .map(this::mapToResponse);
        log.info("Found {} users with first name {}"  , users.getTotalElements() , firstName);

        return users;
    }


    @Override
    public Page<UserResponse> searchByLastName(
            String lastName,
            Pageable pageable
    ){
        log.info("Searching users by last name: {}", lastName);

        Page<UserResponse> users =  userRepository.findByLastNameContainingIgnoreCase(lastName, pageable)
                                                  .map(this::mapToResponse);

        log.info("Found {}  users with last-name {}" , users.getTotalElements() , lastName);

        return users;




    }

    @Override
    public Page<UserResponse> searchByEmail(
            String email,
            Pageable pageable
    ) {

        log.info("Searching users by email: {}", email);
       Page<UserResponse> users =  userRepository
                .findByEmailContainingIgnoreCase(
                        email,
                        pageable
                )
                .map(this::mapToResponse);
        log.info("Found   users with email {}"  , email);

        return users;
    }


    private UserResponse mapToResponse(User user){
        UserResponse response  =  new UserResponse();

        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setEmail(user.getEmail());
        response.setDateOfBirth(user.getDateOfBirth());
        response.setProfileImage(user.getProfileImage());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        return response;
    }
}
