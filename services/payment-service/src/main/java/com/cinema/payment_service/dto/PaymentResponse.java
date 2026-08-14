package com.cinema.payment_service.dto;

import com.cinema.payment_service.entity.PaymentMethod;
import com.cinema.payment_service.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PaymentResponse {
   private UUID id;
   private UUID bookingId;
   private BigDecimal amount;
   private PaymentMethod paymentMethod;
   private PaymentStatus paymentStatus;
   private String  transactionReference;
   private LocalDateTime createdAt;
}
