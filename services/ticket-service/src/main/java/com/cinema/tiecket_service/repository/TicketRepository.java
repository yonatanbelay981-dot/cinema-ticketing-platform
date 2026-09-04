package com.cinema.tiecket_service.repository;

import com.cinema.tiecket_service.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    Optional<Ticket> findByBookingIdAndKeycloakUserId(
            UUID bookingId,
            String keycloakUserId
    );
    Optional<Ticket>findByBookingId(UUID bookingId);
    Optional<Ticket> findByQrCode(String qrCode);
    Optional<Ticket> findByKeycloakUserId(String keycloakUserId);
    Optional<Ticket> findByIdAndKeycloakUserId(
            UUID id,
            String keycloakUserId
    );
    Page<Ticket> findByKeycloakUserId(
            String keycloakUserId,
            Pageable pageable
    );

}
