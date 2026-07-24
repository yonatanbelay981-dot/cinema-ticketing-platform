package com.cinema.movie_services.service;

import com.cinema.movie_services.dto.request.CreateMovieRequest;
import com.cinema.movie_services.dto.request.UpdateMovieRequest;
import com.cinema.movie_services.dto.response.MovieResponse;
import com.cinema.movie_services.entity.MovieStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public interface MovieService {


    Page<MovieResponse> getAllMovies(Pageable pageable);


    MovieResponse createMovie(CreateMovieRequest request);


    MovieResponse getMovieById(UUID id);


    Page<MovieResponse> searchByTitle(
            String title,
            Pageable pageable
    );


    Page<MovieResponse> searchByLanguage(
            String language,
            Pageable pageable
    );


    Page<MovieResponse> findByStatus(
            MovieStatus status,
            Pageable pageable
    );


    Page<MovieResponse> findByGenre(
            String genre,
            Pageable pageable
    );


    MovieResponse updateMovie(
            UUID id,
            UpdateMovieRequest request
    );


    void deleteMovie(UUID id);

}