package com.cinema.cinema_service.repository;

import com.cinema.cinema_service.entity.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CinemaRepository extends JpaRepository<Cinema  , UUID> {
    Cinema findByName(String name);
    Cinema findByAddress(String address);
}
