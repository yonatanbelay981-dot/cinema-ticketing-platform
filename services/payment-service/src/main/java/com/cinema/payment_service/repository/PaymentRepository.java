package com.cinema.payment_service.repository;

import com.cinema.payment_service.entity.Payment;
import com.cinema.payment_service.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment , UUID> {
    Page<Payment> findByBookingIdAndKeycloakUserId(UUID bookingId , String keycloakUserId, Pageable pageable);
    Optional<Payment> findFirstByBookingIdOrderByPaymentAttemptDesc(
            UUID bookingId
    );

    Optional<Payment> findByBookingIdAndPaymentStatus(
            UUID bookingId,
            PaymentStatus paymentStatus
    );
    Optional<Payment> findByPaymentRequestEventId(UUID paymentRequestEventId);
    Optional<Payment>findByIdAndKeycloakUserId(UUID id , String keycloakUserId);
    Page<Payment> findByKeycloakUserId(String keycloakUserId , Pageable pageable);



}
