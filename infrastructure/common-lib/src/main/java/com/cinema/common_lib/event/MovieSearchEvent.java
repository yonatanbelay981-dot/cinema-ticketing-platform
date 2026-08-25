package com.cinema.common_lib;


import com.cinema.search_service.entity.MovieStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class MovieSearchEvent {

    public enum EventType {
        MOVIE_CREATED,
        MOVIE_UPDATED,
        MOVIE_DELETED
    }

    private EventType eventType;

    private UUID movieId;

    private String title;

    private String description;

    private Integer duration;

    private String language;

    private LocalDate releaseDate;

    private String ageRating;

    private String posterUrl;

    private String trailerUrl;

    private MovieStatus status;

    private List<String> genres;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

