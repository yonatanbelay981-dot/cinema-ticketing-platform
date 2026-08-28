package com.cinema.analytics_service.service;


import com.cinema.analytics_service.dto.MovieAnalyticResponse;
import com.cinema.analytics_service.entity.MovieAnalytics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface MovieAnalyticsService {

    MovieAnalyticResponse getMovieAnalytics(UUID movieId  );

    Page<MovieAnalyticResponse> getAllMovieAnalytics(Pageable pageable);

    Page<MovieAnalyticResponse> getPopularMovies(Pageable pageable);
}
