package com.cinema.movie_services.service;

import com.cinema.movie_services.dto.response.GenreResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GenreService {
    Page<GenreResponse> findAllGenres(Pageable pageable);
    GenreResponse createGenre(String name);
    GenreResponse getGenreById(UUID id);
    GenreResponse updateGenre(UUID id, String name);
    void deleteGenre(UUID id);
}
