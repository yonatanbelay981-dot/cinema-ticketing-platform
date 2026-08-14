package com.cinema.tiecket_service.dto;

import com.cinema.tiecket_service.entity.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {



        private UUID id;

        private UUID bookingId;

        private String qrCode;

        private TicketStatus status;

        private LocalDateTime generatedAt;
}
