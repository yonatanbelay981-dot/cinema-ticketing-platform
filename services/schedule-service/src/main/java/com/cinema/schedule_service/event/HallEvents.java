package com.cinema.schedule_service.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HallEvents {
    public enum EventType {
        HALL_CREATED,
        HALL_UPDATED,
        HALL_DELETED
    }

    private EventType eventType;
    private UUID hallId;
    private String name;
    private Integer capacity;

    public HallEvents() {
    }

    public HallEvents(EventType eventType, UUID hallId, String name, Integer capacity) {
        this.eventType = eventType;
        this.hallId = hallId;
        this.name = name;
        this.capacity = capacity;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public UUID getHallId() {
        return hallId;
    }

    public void setHallId(UUID hallId) {
        this.hallId = hallId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
