package com.cinema.cinema_service.repository;

import com.cinema.cinema_service.entity.Cinema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CinemaRepository extends JpaRepository<Cinema  , UUID> {
     Page<Cinema> findByNameContainingIgnoreCase(String name , Pageable pageable);
    Page<Cinema> findByAddressContainingIgnoreCase(String address ,  Pageable pageable);
}
