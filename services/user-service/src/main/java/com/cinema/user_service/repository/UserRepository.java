package com.cinema.user_service.repository;

import com.cinema.user_service.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<User>  findByFirstNameContainingIgnoreCase(String firstname , Pageable pageable);
    Page<User>  findByEmailContainingIgnoreCase(String email , Pageable pageable);
    Page<User> findByLastNameContainingIgnoreCase(String lastname  , Pageable pageable);
    Optional<User> findByKeycloakUserId(String keycloakUserId);
}