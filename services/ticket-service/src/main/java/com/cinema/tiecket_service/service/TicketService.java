package com.cinema.tiecket_service.service;

import com.cinema.tiecket_service.dto.TicketResponse;
import com.cinema.tiecket_service.entity.Ticket;
import com.cinema.tiecket_service.event.PaymentProcessedEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TicketService {
   Page<TicketResponse> getAllTickets(Pageable pageable);
   TicketResponse getTicketById(UUID id  ,String keycloakUserId );
    TicketResponse getTicketByBookingId(UUID bookingId , String keycloakUserId);
    void cancelTicket(UUID ticketId , String keycloakUserId);
    TicketResponse useTicket(UUID ticketId);
     void createTicketFromPayment(PaymentProcessedEvent event) ;
      byte[] generateTicketQrCode(UUID ticketId , String keycloakUserId);
      TicketResponse getByQrcode(String qrCode);


 Page<TicketResponse> getMyTickets(String keycloakUserId, Pageable pageable);
}
