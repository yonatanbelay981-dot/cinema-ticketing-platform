package com.cinema.payment_service.dto;

import com.cinema.payment_service.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentRequest {
    @NotNull(message = "booking Id is required")
   private UUID bookingId;
    @NotNull(message ="choosing payment method is required")
   private PaymentMethod paymentMethod;
}
