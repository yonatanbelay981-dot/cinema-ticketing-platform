package com.cinema.tiecket_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tickets")
    public class Ticket {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        @Column(nullable = false)
    private UUID id;
    @Column(nullable = false )
    private String keycloakUserId;
        @Column(nullable = false  , unique = true)
    private UUID bookingId;
        @Column(nullable = false , unique = true)
    private String qrCode;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;
    @CreationTimestamp
    @Column(nullable = false , updatable = false)
    private LocalDateTime generatedAt;
}
