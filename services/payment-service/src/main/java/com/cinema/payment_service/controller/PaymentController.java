package com.cinema.payment_service.controller;


import com.cinema.payment_service.dto.ApiResponse;
import com.cinema.payment_service.dto.CreatePaymentRequest;
import com.cinema.payment_service.dto.PaymentResponse;
import com.cinema.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getAllPayments(
            @PageableDefault(size = 10) Pageable pageable
    ) {

        log.info("Request received to fetch all payments");

        Page<PaymentResponse> payments =
                paymentService.getAllPayments(pageable);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Payments fetched successfully",
                        payments
                )
        );
    }


    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getPayments(
            @AuthenticationPrincipal Jwt jwt,
            @PageableDefault(size = 10) Pageable pageable
    ) {

        log.info(
                "Request received to fetch payment with keycloakUserId {}",
                jwt.getSubject()
        );
        String keycloakUserId =  jwt.getSubject();

        Page<PaymentResponse> payments =
                paymentService.getPayments(keycloakUserId , pageable);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Payment fetched successfully",
                        payments
                )
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt

    ) {

        log.info(
                "Request received to fetch payment with id {}",
                id
        );
        String keycloakUserId =  jwt.getSubject();

        PaymentResponse payment =
                paymentService.getPaymentById(id , keycloakUserId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Payment fetched successfully",
                        payment
                )
        );
    }







    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getPaymentsByBookingId(
            @PathVariable UUID bookingId,
            @PageableDefault(size = 10) Pageable pageable,
            @AuthenticationPrincipal Jwt jwt

    ) {

        log.info(
                "Request received to fetch payments for booking {}",
                bookingId
        );
        String keycloakUserId = jwt.getSubject();

        Page<PaymentResponse> payments =
                paymentService.getPaymentsByBookingId(
                        bookingId,
                        keycloakUserId,

                        pageable
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Payments for booking fetched successfully",
                        payments
                )
        );
    }
@PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deletePaymentById(
            @PathVariable UUID id
    ) {

        log.info(
                "Request received to delete payment with id {}",
                id
        );

        paymentService.deletePaymentById(id);

        return ResponseEntity.noContent().build();
    }
}