package com.cinema.cinema_service.repository;

import com.cinema.cinema_service.entity.Hall;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HallRepository extends JpaRepository<Hall, UUID> {
    Hall findByName(String name);
}
