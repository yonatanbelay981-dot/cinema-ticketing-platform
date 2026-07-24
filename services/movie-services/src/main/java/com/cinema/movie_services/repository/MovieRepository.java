package com.cinema.movie_services.repository;

import com.cinema.movie_services.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {

}
