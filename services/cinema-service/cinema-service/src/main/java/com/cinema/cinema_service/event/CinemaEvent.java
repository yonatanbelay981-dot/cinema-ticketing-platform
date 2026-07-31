package com.cinema.cinema_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CinemaEvent {


    public enum  EventType
    {
        CINEMA_CREATED,
        CINEMA_UPDATED,
        CINEMA_DELETED
    }
    private EventType eventType;
    private UUID cinemaId;
    private String name;
    private String address;

}
