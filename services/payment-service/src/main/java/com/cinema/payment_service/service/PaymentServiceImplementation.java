package com.cinema.payment_service.service;
import com.cinema.payment_service.dto.PaymentResponse;
import com.cinema.payment_service.entity.Payment;
import com.cinema.payment_service.entity.PaymentMethod;
import com.cinema.payment_service.entity.PaymentStatus;
import com.cinema.payment_service.event.BookingPaymentRequestedEvent;
import com.cinema.payment_service.event.PaymentProcessedEvent;
import com.cinema.payment_service.exception.PaymentNotFoundException;
import com.cinema.payment_service.repository.PaymentRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class PaymentServiceImplementation implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaPaymentProducer kafkaPaymentProducer;

    public PaymentServiceImplementation(PaymentRepository paymentRepository, KafkaPaymentProducer kafkaPaymentProducer) {
        this.paymentRepository = paymentRepository;
        this.kafkaPaymentProducer = kafkaPaymentProducer;
    }

    @Override
    public Page<PaymentResponse> getAllPayments(Pageable pageable) {

        log.info("Fetching all payments");

        Page<Payment> payments = paymentRepository.findAll(pageable);

        log.info("Successfully fetched {} payments", payments.getNumberOfElements());

        return payments.map(this::mapToPaymentResponse);
    }

    @Override
    public PaymentResponse getPaymentById(UUID id) {

        log.info("Fetching payment with id {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Payment with id {} was not found", id);
                    return new PaymentNotFoundException(
                            "Payment with id " + id + " was not found"
                    );
                });

        log.info("Payment with id {} found successfully", id);

        return mapToPaymentResponse(payment);
    }



    @Override
    public Page<PaymentResponse> getPaymentsByBookingId(
            UUID bookingId,
            Pageable pageable
    ) {

        log.info(
                "Fetching payments for booking {}",
                bookingId
        );

        Page<Payment> payments =
                paymentRepository.findByBookingId(
                        bookingId,
                        pageable
                );

        log.info(
                "Successfully fetched payments for booking {}",
                bookingId
        );

        return payments.map(this::mapToPaymentResponse);
    }

    @Override
    public void deletePaymentById(UUID id) {

        log.info(
                "Deleting payment with id {}",
                id
        );

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn(
                            "Payment with id {} was not found while deleting",
                            id
                    );


                    return new PaymentNotFoundException(
                            "Payment with id " + id + " was not found"
                    );
                });

        paymentRepository.delete(payment);

        log.info(
                "Payment with id {} deleted successfully",
                id
        );
    }

    @Override
    public void processPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> {
            log.warn("payment not found with id {} ", paymentId);
            return new PaymentNotFoundException(
                    "Payment not found: " + paymentId
            );
        });

        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            log.warn(
                    "Payment {} is already processed with status {}",
                    paymentId,
                    payment.getPaymentStatus()
            );
            return;
        }
        payment.setPaymentStatus(PaymentStatus.PROCESSING);

        Payment processingPayment =
                paymentRepository.save(payment);

        log.info(
                "Payment {} is now PROCESSING",
                processingPayment.getId()
        );


        boolean paymentSuccessful = true;
        if (paymentSuccessful) {
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            Payment savedPayment = paymentRepository.save(payment);
            log.info(
                    "Payment {} completed successfully",
                    savedPayment.getId()
            );
            publishPaymentProcessedEvent(savedPayment);
        } else {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            Payment failedPayment =
                    paymentRepository.save(payment);

            log.warn(
                    "Payment {} failed",
                    failedPayment.getId()
            );
            publishPaymentProcessedEvent(failedPayment);
        }


    }
    private void publishPaymentProcessedEvent(Payment payment) {

        PaymentProcessedEvent event =
                new PaymentProcessedEvent(
                        payment.getId(),
                        payment.getBookingId(),
                        payment.getAmount(),
                        payment.getPaymentStatus(),
                        payment.getTransactionReference()
                );

        CompletableFuture<SendResult<String, Object>> future =
                kafkaPaymentProducer.publish(
                        payment.getBookingId(),
                        event
                );

        future.thenAccept(result ->
                log.info(
                        "PAYMENT_PROCESSED event published successfully for booking {}",
                        payment.getBookingId()
                )
        ).exceptionally(ex -> {

            log.error(
                    "Failed to publish PAYMENT_PROCESSED event for booking {}",
                    payment.getBookingId(),
                    ex
            );

            return null;
        });
    }

    @Override
    public Payment createPaymentFromBooking(BookingPaymentRequestedEvent event) {

        log.info(
                "Creating payment from Booking Service for booking {}",
                event.bookingId()
        );

        Optional<Payment> existingPayment =
                paymentRepository.findByPaymentRequestEventId(
                        event.eventId()
                );

        if (existingPayment.isPresent()) {

            log.info(
                    "Duplicate payment request event {} received for booking {}",
                    event.eventId(),
                    event.bookingId()
            );

            return existingPayment.get();
        }

        Optional<Payment> successfulPayment =
                paymentRepository.findByBookingIdAndPaymentStatus(
                        event.bookingId(),
                        PaymentStatus.SUCCESS
                );

        if (successfulPayment.isPresent()) {

            log.info(
                    "Booking {} already has a successful payment {}",
                    event.bookingId(),
                    successfulPayment.get().getId()
            );

            return successfulPayment.get();
        }
        int nextAttempt = paymentRepository.findFirstByBookingIdOrderByPaymentAttemptDesc(
                event.bookingId()
        ).map(payment -> payment.getPaymentAttempt() + 1).orElse(1);


            Payment payment = new Payment();
        payment.setPaymentAttempt(nextAttempt);
        payment.setBookingId(event.bookingId());
        payment.setPaymentRequestEventId(event.eventId());
        payment.setAmount(event.totalPrice());
        payment.setTransactionReference("pay-" + UUID.randomUUID());
        payment.setPaymentMethod(
                PaymentMethod.valueOf(event.paymentMethod())
        );
        payment.setPaymentStatus(PaymentStatus.PENDING);

        Payment savedPayment =
                paymentRepository.save(payment);

        log.info(
                "Payment {} created for booking {}",
                savedPayment.getId(),
                savedPayment.getBookingId()
        );

        return savedPayment;
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {

        return new PaymentResponse(
                payment.getId(),
                payment.getBookingId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                payment.getTransactionReference(),
                payment.getCreatedAt()
        );
    }
}
