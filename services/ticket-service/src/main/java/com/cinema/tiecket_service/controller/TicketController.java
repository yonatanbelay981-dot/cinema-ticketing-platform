package com.cinema.tiecket_service.controller;
import com.cinema.tiecket_service.dto.ApiResponse;
import com.cinema.tiecket_service.dto.TicketResponse;
import com.cinema.tiecket_service.service.TicketService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/tickets")
public class  TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TicketResponse>>> getAllTickets(
            Pageable pageable
    ) {

        Page<TicketResponse> tickets =
                ticketService.getAllTickets(pageable);

        ApiResponse<Page<TicketResponse>> response =
                new ApiResponse<>(
                        true,
                        "Tickets retrieved successfully",
                        tickets
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketResponse>> getTicketById(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String keycloakUserId = jwt.getSubject();

        TicketResponse ticket =
                ticketService.getTicketById(id, keycloakUserId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Ticket retrieved successfully",
                        ticket
                )
        );
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<TicketResponse>>> getTicketById(
            @AuthenticationPrincipal Jwt jwt,
            Pageable pageable
    ) {
        String keycloakUserId = jwt.getSubject();

        Page<TicketResponse > tickets =
                ticketService.getMyTickets( keycloakUserId , pageable);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Ticket retrieved successfully",
                        tickets
                )
        );
    }


    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<TicketResponse>> getTicketByBookingId(
            @PathVariable UUID bookingId,
            @AuthenticationPrincipal Jwt jwt
    ) {

        String keycloakUserId = jwt.getSubject();

        TicketResponse ticket =
                ticketService.getTicketByBookingId(bookingId , keycloakUserId);

        ApiResponse<TicketResponse> response =
                new ApiResponse<>(
                        true,
                        "Ticket retrieved successfully",
                        ticket
                );

        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @PostMapping("/qr/{qrCode}/validate")
    public ResponseEntity<ApiResponse<TicketResponse>> validateTicketByQrCode(
            @PathVariable String qrCode
    ) {

        TicketResponse ticket =
                ticketService.getByQrcode(qrCode);

        ApiResponse<TicketResponse> response =
                new ApiResponse<>(
                        true,
                        "Ticket validated successfully",
                        ticket
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{ticketId}/qr")
    public ResponseEntity<byte[]> generateTicketQrCode(
            @PathVariable UUID ticketId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String keycloakUserId = jwt.getSubject();

        byte[] qrCode =
                ticketService.generateTicketQrCode(ticketId , keycloakUserId);

        return ResponseEntity.ok()
                .header("Content-Type", "image/png")
                .body(qrCode);
    }

    @PatchMapping("/{ticketId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelTicket(
            @PathVariable UUID ticketId ,
            @AuthenticationPrincipal Jwt jwt

    ) {

       String keycloakUserId = jwt.getSubject();
        ticketService.cancelTicket(ticketId ,  keycloakUserId);

        ApiResponse<Void> response =
                new ApiResponse<>(
                        true,
                        "Ticket cancelled successfully",
                        null
                );

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @PatchMapping("/{ticketId}/use")
    public ResponseEntity<ApiResponse<TicketResponse>> useTicket(
            @PathVariable UUID ticketId
    ) {

        TicketResponse ticket =
                ticketService.useTicket(ticketId);

        ApiResponse<TicketResponse> response =
                new ApiResponse<>(
                        true,
                        "Ticket marked as used successfully",
                        ticket
                );

        return ResponseEntity.ok(response);
    }
}