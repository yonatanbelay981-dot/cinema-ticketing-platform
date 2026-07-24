package com.cinema.movie_services.dto.request;

import com.cinema.movie_services.entity.MovieStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateMovieRequest {


    @NotBlank(message = "Title cannot be blank")
    private String title;


    private String description;


    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be greater than zero")
    private Integer duration;


    @NotBlank(message = "Language is required")
    private String language;


    @NotNull(message = "Release date is required")
    private LocalDate releaseDate;


    @NotBlank(message = "Age rating is required")
    private String ageRating;


    private String posterUrl;


    private String trailerUrl;


    @NotNull(message = "Status is required")
    private MovieStatus status;


    @NotEmpty(message = "At least one genre is required")
    private List<UUID> genreIds;

}