package com.cinema.seat_service.repository;

import com.cinema.seat_service.entity.ShowTimeCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShowtimeCacheRepository extends JpaRepository<ShowTimeCache, UUID> {

}
