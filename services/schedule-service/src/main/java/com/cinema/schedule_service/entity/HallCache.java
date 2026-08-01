package com.cinema.schedule_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hall_cache")
public class HallCache {
    private String eventType;

    @Id
    private UUID hallId;

    private String name;

    private Integer capacity;
}
