package com.cinema.analytics_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
        name = "sales_analytics",
        indexes = {
                @Index(
                        name = "idx_sales_analytics_date",
                        columnList = "date"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalesAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private LocalDate date;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalRevenue;

    @Column(nullable = false)
    private Long totalTicketsSold;

    @Column(nullable = false)
    private Long totalBookings;
}
