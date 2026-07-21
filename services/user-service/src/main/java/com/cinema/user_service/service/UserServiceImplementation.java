package com.cinema.user_service.service;

import com.cinema.user_service.dto.request.CreateUserRequest;
import com.cinema.user_service.dto.request.UpdateUserRequest;
import com.cinema.user_service.dto.response.UserResponse;
import com.cinema.user_service.entity.User;
import com.cinema.user_service.exception.UserNotFoundException;
import com.cinema.user_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Slf4j
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;

    public UserServiceImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public UserResponse getById(Long id) {
        log.info("Fetching user with id {}"  , id);
        User user = userRepository.findById(id).orElseThrow(()->{ log.warn("User with id {} not found", id);
            return new UserNotFoundException("user  not found with id " +  id);});
        return mapToResponse(user);
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating user with email {}"  , request.getEmail());
        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDateOfBirth(request.getDateOfBirth());


        User savedUser = userRepository.save(user);
        log.info("user successfully created with id {} "  , savedUser.getId());


        return mapToResponse(savedUser);
    }

    @Override
    public UserResponse updateUserById(Long id, UpdateUserRequest request) {
        log.info("Updating user with id {}", id);
        User user = userRepository.findById(id).orElseThrow(()->{ log.warn("User not found with id  {}", id);
            return new UserNotFoundException("user not found with id : " + id);});
        
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setProfileImage(request.getProfileImage());
        user.setEmail(request.getEmail());

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
    public void deleteUserById(Long id) {
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
    public UserResponse mapToResponse(User user){
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
