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


    @GetMapping
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


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(
            @PathVariable UUID id
    ) {

        log.info(
                "Request received to fetch payment with id {}",
                id
        );

        PaymentResponse payment =
                paymentService.getPaymentById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Payment fetched successfully",
                        payment
                )
        );
    }


    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @Valid @RequestBody CreatePaymentRequest request
    ) {

        log.info(
                "Request received to create payment for booking {}",
                request.getBookingId()
        );

        PaymentResponse payment =
                paymentService.createPayment(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Payment created successfully",
                                payment
                        )
                );
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getPaymentsByBookingId(
            @PathVariable UUID bookingId,
            @PageableDefault(size = 10) Pageable pageable
    ) {

        log.info(
                "Request received to fetch payments for booking {}",
                bookingId
        );

        Page<PaymentResponse> payments =
                paymentService.getPaymentsByBookingId(
                        bookingId,
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

    @DeleteMapping("/{id}")
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