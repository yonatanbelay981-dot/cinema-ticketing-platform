package com.cinema.Booking_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LockSeatsRequestedEvent {
    public   enum EventType{
        LOCK_SEATS_REQUESTED,


    }

    private UUID bookingId;
    private UUID showTimeId;
    private List<UUID> seatIds;
    private String keycloakUserId;
    private EventType eventType;
}
