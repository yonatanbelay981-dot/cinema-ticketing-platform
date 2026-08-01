package com.cinema.schedule_service.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieEvent {
    public enum   EventType {
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
