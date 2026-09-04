package com.cinema.payment_service.service;

import com.cinema.payment_service.dto.CreatePaymentRequest;
import com.cinema.payment_service.dto.PaymentResponse;
import com.cinema.payment_service.entity.Payment;
import com.cinema.payment_service.event.BookingPaymentRequestedEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;


public interface PaymentService {
    Page<PaymentResponse>getAllPayments(Pageable pageable);
    PaymentResponse getPaymentById(UUID id , String keycloakUserId);
    Page<PaymentResponse> getPaymentsByBookingId(
            UUID bookingId,
            String keycloakUserId,
            Pageable pageable
    );
    void deletePaymentById(UUID id);
    void processPayment(UUID paymentId);

    Payment createPaymentFromBooking(BookingPaymentRequestedEvent event);

    Page<PaymentResponse> getPayments(String keycloakUserId , Pageable pageable);
}
