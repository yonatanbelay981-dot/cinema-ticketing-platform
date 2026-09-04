package com.cinema.movie_services.controller;

import com.cinema.movie_services.dto.request.CreateGenreRequest;
import com.cinema.movie_services.dto.request.UpdateGenreRequest;
import com.cinema.movie_services.dto.response.ApiResponse;
import com.cinema.movie_services.dto.response.GenreResponse;
import com.cinema.movie_services.service.GenreService;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }


    @GetMapping
    public ResponseEntity<ApiResponse<Page<GenreResponse>>> getAllGenres(
            Pageable pageable
    ) {

        Page<GenreResponse> genres = genreService.findAllGenres(pageable);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Genres retrieved successfully",
                        genres
                )
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GenreResponse>> getGenreById(
            @PathVariable UUID id
    ) {

        GenreResponse genre = genreService.getGenreById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Genre retrieved successfully",
                        genre
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<GenreResponse>> createGenre(
            @Valid @RequestBody CreateGenreRequest request
    ) {

        GenreResponse genre = genreService.createGenre(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Genre created successfully",
                                genre
                        )
                );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GenreResponse>> updateGenre(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateGenreRequest request
    ) {

        GenreResponse genre = genreService.updateGenre(id, request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Genre updated successfully",
                        genre
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGenre(
            @PathVariable UUID id
    ) {

        genreService.deleteGenre(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Genre deleted successfully",
                        null
                )
        );
    }
}
