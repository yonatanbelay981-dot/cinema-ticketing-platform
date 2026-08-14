package com.cinema.tiecket_service.repository;

import com.cinema.tiecket_service.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    Optional<Ticket> findByBookingId(UUID bookingId);
    Optional<Ticket> findByQrCode(String qrCode);
}
