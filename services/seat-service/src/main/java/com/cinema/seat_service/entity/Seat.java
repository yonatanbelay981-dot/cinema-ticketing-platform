package com.cinema.seat_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "seats",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_hall_row_seat",
                        columnNames = {"hallId", "rowName", "seatNumber"}
                )
        },
        indexes = {
                @Index(name = "idx_seat_hall_id", columnList = "hallId"),
                @Index(name = "idx_seat_row_name", columnList = "rowName"),
                @Index(name = "idx_seat_seat_number", columnList = "seatNumber")
        }
)
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID hallId;

    @Column(nullable = false)
    private String rowName;

    @Column(nullable = false)
    private Integer seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatType seatType;
}

