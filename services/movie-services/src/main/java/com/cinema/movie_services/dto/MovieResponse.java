package com.cinema.movie_services.dto;



import com.cinema.movie_services.entity.MovieStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponse {


    private UUID id;


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
