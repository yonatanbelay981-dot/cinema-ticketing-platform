package com.cinema.movie_services.controller;

import com.cinema.movie_services.dto.request.CreateMovieRequest;
import com.cinema.movie_services.dto.request.UpdateMovieRequest;
import com.cinema.movie_services.dto.response.ApiResponse;
import com.cinema.movie_services.dto.response.MovieResponse;
import com.cinema.movie_services.entity.MovieStatus;
import com.cinema.movie_services.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MovieResponse>>> getAllMovies(@PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<MovieResponse> movies = movieService.getAllMovies(pageable);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Movies retrieved successfully",
                        movies
                )
        );
    }



    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<MovieResponse>>> searchMovies(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) MovieStatus status,
            @RequestParam(required = false) String genre,
            @PageableDefault(
                    size = 10,
                    page = 0,
                    sort = "id",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable

    ) {

        Page<MovieResponse> movies;

        if (title != null && !title.isBlank()) {
            movies = movieService.searchByTitle(title, pageable);

        } else if (language != null && !language.isBlank()) {
            movies = movieService.searchByLanguage(language, pageable);

        } else if (status != null) {
            movies = movieService.findByStatus(status, pageable);

        } else if (genre != null && !genre.isBlank()) {
            movies = movieService.findByGenre(genre, pageable);

        } else {
            movies = movieService.getAllMovies(pageable);
        }

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Movies retrieved successfully",
                        movies
                )
        );
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> getMovieById(@PathVariable  UUID id) {
        MovieResponse movie = movieService.getMovieById(id);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Movie retrieved successfully",
                        movie
                )
        );
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<MovieResponse>> createMovie(@Valid @RequestBody CreateMovieRequest movieRequest) {
        MovieResponse movie = movieService.createMovie(movieRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(


                new ApiResponse<>(
                        true,
                        "Movie created successfully",
                        movie
                )

        );
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> updateMovie(@PathVariable UUID id, @Valid @RequestBody UpdateMovieRequest movieRequest) {
        MovieResponse movie = movieService.updateMovie(id, movieRequest);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Movie updated successfully",
                        movie
                )
        );
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMovie(@PathVariable UUID id) {
        movieService.deleteMovie(id);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Movie deleted successfully",
                        null
                )
        );
    }

}