package com.cinema.movie_services.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieEvent {

    public enum EventType {
        MOVIE_CREATED,
        MOVIE_UPDATED,
        MOVIE_DELETED
    }

    private EventType eventType;

    private UUID movieId;

    private String title;

    private Integer duration;

    private String language;

}
