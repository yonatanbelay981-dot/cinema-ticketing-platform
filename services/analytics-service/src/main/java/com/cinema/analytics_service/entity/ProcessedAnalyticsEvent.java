package com.cinema.analytics_service.entity;




import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "processed_analytics_events",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_event_processor",
                        columnNames = {"event_id", "processor_type"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedAnalyticsEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "processor_type", nullable = false)
    private String processorType;

    @Column(nullable = false)
    private LocalDateTime processedAt;
}
