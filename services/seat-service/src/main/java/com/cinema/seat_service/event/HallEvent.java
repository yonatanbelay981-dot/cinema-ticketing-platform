package com.cinema.seat_service.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HallEvent {
    public enum EventType {
        HALL_CREATED,
        HALL_UPDATED,
        HALL_DELETED
    }

    private EventType eventType;
    private UUID hallId;
    private String name;
    private Integer capacity;

}
