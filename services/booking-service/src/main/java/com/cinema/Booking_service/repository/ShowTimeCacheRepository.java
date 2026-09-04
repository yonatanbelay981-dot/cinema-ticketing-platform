package com.cinema.Booking_service.repository;

import com.cinema.Booking_service.entity.ShowTimeCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShowTimeCacheRepository extends JpaRepository<ShowTimeCache, UUID> {
}
