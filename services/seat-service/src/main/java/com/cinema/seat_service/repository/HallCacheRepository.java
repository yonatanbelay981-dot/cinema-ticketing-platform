package com.cinema.seat_service.repository;

import com.cinema.seat_service.entity.HallCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HallCacheRepository extends JpaRepository<HallCache, UUID> {
}
