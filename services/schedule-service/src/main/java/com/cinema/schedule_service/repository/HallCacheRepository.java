package com.cinema.schedule_service.repository;

import com.cinema.schedule_service.entity.HallCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HallCacheRepository extends JpaRepository<HallCache , UUID> {
}
