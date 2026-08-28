package com.cinema.analytics_service.repository;

import com.cinema.analytics_service.dto.MovieAnalyticResponse;
import com.cinema.analytics_service.entity.MovieAnalytics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MovieAnalyticsRepository extends JpaRepository<MovieAnalytics , UUID> {
    Optional<MovieAnalytics> findByMovieId(UUID movieId);
    Page<MovieAnalytics> findAllByOrderByTicketsSoldDesc(Pageable pageable);
}
