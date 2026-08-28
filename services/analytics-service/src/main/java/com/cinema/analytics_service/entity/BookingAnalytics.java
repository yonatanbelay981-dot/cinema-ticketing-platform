package com.cinema.analytics_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "booking_analytics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private LocalDate date;

    @Column(nullable = false)
    private Long totalBookings;

    @Column(nullable = false)
    private Long confirmedBookings;

    @Column(nullable = false)
    private Long failedBookings;

    @Column(nullable = false)
    private Long cancelledBookings;
}