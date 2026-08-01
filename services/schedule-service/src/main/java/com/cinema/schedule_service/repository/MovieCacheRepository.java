package com.cinema.schedule_service.repository;

import com.cinema.schedule_service.entity.MovieCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MovieCacheRepository extends JpaRepository<MovieCache , UUID> {

}
