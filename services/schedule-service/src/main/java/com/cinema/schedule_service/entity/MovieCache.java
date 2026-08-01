package com.cinema.schedule_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movie_cache")
public class MovieCache {
    private String eventType;

    @Id
    private UUID movieId;

    private String title;

    private Integer duration;

    private String language;
}
