package com.cinema.Booking_service.services;

import com.cinema.Booking_service.event.BookingConfirmedEvent;
import com.cinema.Booking_service.event.BookingPaymentRequestedEvent;
import com.cinema.Booking_service.event.ShowtimePriceRequestedEvent;
import com.cinema.common_lib.event.BookingAnalyticsEvent;
import com.cinema.common_lib.event.BookingStatusAnalyticsEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
@Service
@Slf4j
public class KafkaBookingProducer  {

    @Value("${app.kafka.topic.booking-event}")
    private String topic;
    @Value("${app.kafka.topic.showtime-price-requests}")
    private String showtimePriceRequestTopic;
    @Value("${app.kafka.topic.booking-payment-topic}")
    private String bookingPaymentTopic;
    @Value("${app.kafka.topic.booking-analytic-topic}")
    private String bookingAnalyticTopic;
    @Value("${app.kafka.topic.booking-status-analytic-topic}")
    private String bookingStatusAnalyticTopic;
    @Value("${app.kafka.topic.booking-confirmed-topic}")
    private String bookingConfirmedTopic;


    private final KafkaTemplate<String , Object> kafkaTemplate;

    public KafkaBookingProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public <T>CompletableFuture<SendResult<String  , Object>> publish(UUID bookingId , T event){
        CompletableFuture<SendResult<String , Object>> future = kafkaTemplate.send(topic , bookingId.toString() , event);

        future.whenComplete((result , ex)->{

            if(ex==null){

                log.info("Successfully published booking event with bookingId: {} to topic: {}", bookingId, topic);
            }

            else{
                log.error("Failed to publish booking event with bookingId: {} to topic: {}", bookingId, topic, ex);
            }
        });
        return future;
    }

    public CompletableFuture<SendResult<String, Object>> publishShowtimePriceRequest(
            UUID bookingId,
            ShowtimePriceRequestedEvent event
    ) {

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(
                        showtimePriceRequestTopic,
                        bookingId.toString(),
                        event
                );

        future.whenComplete((result, ex) -> {

            if (ex == null) {
                log.info(
                        "SHOWTIME_PRICE_REQUESTED published successfully. bookingId={}",
                        bookingId
                );
            } else {
                log.error(
                        "Failed to publish SHOWTIME_PRICE_REQUESTED. bookingId={}",
                        bookingId,
                        ex
                );
            }
        });

        return future;
    }

    public CompletableFuture<SendResult<String , Object>> publishBookingPayment(UUID bookingId , BookingPaymentRequestedEvent event){
        CompletableFuture<SendResult<String ,Object>> future = kafkaTemplate.send(bookingPaymentTopic  , bookingId.toString() , event);
        future.whenComplete((result , ex)->{
            if (ex == null) {
                log.info(
                        "BOOKING_PAYMENT published successfully. bookingId={}",
                        bookingId
                );
            } else {
                log.error(
                        "Failed to publish BOOKING_PAYMENT. bookingId={}",
                        bookingId,
                        ex
                );
            }
        });
        return future;
    }

    public CompletableFuture<SendResult<String , Object>> publishBookingAnalyticsEvent(UUID bookingId , BookingAnalyticsEvent event) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(bookingAnalyticTopic, bookingId.toString(), event);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info(
                        "Booking-Analytic-Event published successfully. bookingId={}",
                        bookingId
                );
            } else {
                log.error(
                        "Failed to publish Booking-Analytic-Event bookingId={}",
                        bookingId,
                        ex
                );
            }
        });
        return future;
    }
        public CompletableFuture<SendResult<String , Object>> publishBookingStatusAndAnalyticsEvent(UUID bookingId , BookingStatusAnalyticsEvent
        event){
            CompletableFuture<SendResult<String ,Object>> future = kafkaTemplate.send(bookingStatusAnalyticTopic  , bookingId.toString() , event);
            future.whenComplete((result , ex)->{
                if (ex == null) {
                    log.info(
                            "Booking-Status-And-Analytic-Event published successfully. bookingId={}",
                            bookingId
                    );
                } else {
                    log.error(
                            "Failed to publishBooking-Status-And-Analytic-Event bookingId={}",
                            bookingId,
                            ex
                    );
                }
            });
            return future;
    }
    public CompletableFuture<SendResult<String, Object>> publishBookingConfirmed(
            UUID bookingId,
            BookingConfirmedEvent event
    ) {
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(
                        bookingConfirmedTopic,
                        bookingId.toString(),
                        event
                );

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info(
                        "Successfully published BOOKING_CONFIRMED for bookingId: {} to topic: {}",
                        bookingId,
                        bookingConfirmedTopic
                );
            } else {
                log.error(
                        "Failed to publish BOOKING_CONFIRMED for bookingId: {} to topic: {}",
                        bookingId,
                        bookingConfirmedTopic,
                        ex
                );
            }
        });

        return future;
    }

}
