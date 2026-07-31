package com.cinema.cinema_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HallEvent {

    public enum EventType
    {
        HALL_CREATED,
        HALL_UPDATED,
        HALL_DELETED
    }
    private EventType eventType;
    private UUID hallId;
    private String name;
    private Integer capacity;

}
