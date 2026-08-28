package com.cinema.analytics_service.controller;


import com.cinema.analytics_service.dto.ApiResponse;
import com.cinema.analytics_service.dto.MovieAnalyticResponse;
import com.cinema.analytics_service.service.MovieAnalyticsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/analytics")
public class MovieAnalyticsController {

    private final MovieAnalyticsService movieAnalyticsService;

    public MovieAnalyticsController(
            MovieAnalyticsService movieAnalyticsService
    ) {
        this.movieAnalyticsService = movieAnalyticsService;
    }

    @GetMapping("/movies/{movieId}")
    public ResponseEntity<ApiResponse<MovieAnalyticResponse>> getMovieAnalytics(
            @PathVariable UUID movieId
    ) {

        MovieAnalyticResponse analytics =
                movieAnalyticsService.getMovieAnalytics(movieId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Movie analytics fetched successfully",
                        analytics
                )
        );
    }

    @GetMapping("/movies")
    public ResponseEntity<ApiResponse<Page<MovieAnalyticResponse>>> getAllMovieAnalytics(
            Pageable pageable
    ) {

        Page<MovieAnalyticResponse> analytics =
                movieAnalyticsService.getAllMovieAnalytics(pageable);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Movie analytics fetched successfully",
                        analytics
                )
        );
    }

    @GetMapping("/movies/popular")
    public ResponseEntity<ApiResponse<Page<MovieAnalyticResponse>>> getPopularMovies(
            Pageable pageable
    ) {

        Page<MovieAnalyticResponse> popularMovies =
                movieAnalyticsService.getPopularMovies(pageable);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Popular movies fetched successfully",
                        popularMovies
                )
        );
    }
}
