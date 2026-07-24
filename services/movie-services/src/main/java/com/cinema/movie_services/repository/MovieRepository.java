package com.cinema.movie_services.repository;

import com.cinema.movie_services.entity.Movie;
import com.cinema.movie_services.entity.MovieStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MovieRepository extends JpaRepository<Movie, UUID> {
    Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Movie> findByStatus(MovieStatus status, Pageable pageable);
    Page<Movie> findByLanguageContainingIgnoreCase(String language, Pageable pageable);

    Page<Movie> findByGenres_NameContainingIgnoreCase(
            String genre,
            Pageable pageable
    );
}

