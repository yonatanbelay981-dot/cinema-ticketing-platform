package com.cinema.user_service.service;

import com.cinema.user_service.dto.request.CreateUserRequest;
import com.cinema.user_service.dto.request.UpdateUserRequest;
import com.cinema.user_service.dto.response.UserResponse;
import com.cinema.user_service.entity.User;
import com.cinema.user_service.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;

    public UserServiceImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public UserResponse getById(Long id) {
        User user = userRepository.findById(id).orElseThrow(()->new RuntimeException("user  not found with id " +  id));
        return mapToResponse(user);
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDateOfBirth(request.getDateOfBirth());


        User savedUser = userRepository.save(user);


        return mapToResponse(savedUser);
    }

    @Override
    public UserResponse updateUserById(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id).orElseThrow(()->new UsernameNotFoundException("user not found with id : " + id));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setProfileImage(request.getProfileImage());
        user.setEmail(request.getEmail());

        User updatedUser = userRepository.save(user);

        return mapToResponse(updatedUser) ;

    }

    @Override
    public UserResponse findUserByEmail(String email) {
        User user  =  userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("user not found with email" +  email));
        return mapToResponse(user);
    }

    @Override
    public void deleteUserById(Long id) {

        userRepository.deleteById(id);

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
