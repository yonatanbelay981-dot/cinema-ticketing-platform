package com.cinema.movie_services.service;

import com.cinema.movie_services.dto.request.CreateGenreRequest;
import com.cinema.movie_services.dto.request.UpdateGenreRequest;
import com.cinema.movie_services.dto.response.GenreResponse;

import com.cinema.movie_services.entity.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GenreService {
    Page<GenreResponse> findAllGenres(Pageable pageable);
    GenreResponse createGenre(CreateGenreRequest request);
    GenreResponse getGenreById(UUID id);
    GenreResponse updateGenre(UUID id, UpdateGenreRequest request);
    void deleteGenre(UUID id);
}
