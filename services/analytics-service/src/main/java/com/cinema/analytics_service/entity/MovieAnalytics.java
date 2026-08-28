package com.cinema.analytics_service.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "movie_analytics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "movie_id", nullable = false, unique = true)
    private UUID movieId;

    @Column(name = "movie_title", nullable = false)
    private String movieTitle;

    @Column(name = "total_bookings", nullable = false)
    private Long totalBookings = 0L;

    @Column(name = "tickets_sold", nullable = false)
    private Long ticketsSold = 0L;

    @Column(name = "total_revenue", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalRevenue = BigDecimal.ZERO;
}


