package com.cinema.seat_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "seat_reservations" ,
       uniqueConstraints = {
        @UniqueConstraint(name = "uk_seat_reservation", columnNames = {"showtimeId", "seatId"})
       },
indexes = {
        @Index(name = "idx_seat_reservation_showtime_id", columnList = "showtimeId")
})
public class SeatReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private UUID showtimeId;
    @Column(nullable = false)
    private UUID seatId;
    @Column(nullable = false)
    private UUID userId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;
    private LocalDateTime lockExpiredAt;
}
