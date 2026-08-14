package com.cinema.tiecket_service.controller;
import com.cinema.tiecket_service.dto.ApiResponse;
import com.cinema.tiecket_service.dto.TicketResponse;
import com.cinema.tiecket_service.service.TicketService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

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
            @PathVariable UUID id
    ) {

        TicketResponse ticket =
                ticketService.getTicketById(id);

        ApiResponse<TicketResponse> response =
                new ApiResponse<>(
                        true,
                        "Ticket retrieved successfully",
                        ticket
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<TicketResponse>> getTicketByBookingId(
            @PathVariable UUID bookingId
    ) {

        TicketResponse ticket =
                ticketService.getTicketByBookingId(bookingId);

        ApiResponse<TicketResponse> response =
                new ApiResponse<>(
                        true,
                        "Ticket retrieved successfully",
                        ticket
                );

        return ResponseEntity.ok(response);
    }

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
            @PathVariable UUID ticketId
    ) {

        byte[] qrCode =
                ticketService.generateTicketQrCode(ticketId);

        return ResponseEntity.ok()
                .header("Content-Type", "image/png")
                .body(qrCode);
    }

    @PatchMapping("/{ticketId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelTicket(
            @PathVariable UUID ticketId
    ) {

        ticketService.cancelTicket(ticketId);

        ApiResponse<Void> response =
                new ApiResponse<>(
                        true,
                        "Ticket cancelled successfully",
                        null
                );

        return ResponseEntity.ok(response);
    }

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