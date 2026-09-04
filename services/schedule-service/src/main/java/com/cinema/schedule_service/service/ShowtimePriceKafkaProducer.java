package com.cinema.schedule_service.service;

import com.cinema.schedule_service.event.ShowtimePriceResponseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ShowtimePriceKafkaProducer {

    private static final String TOPIC = "showtime-price-responses";

    private final KafkaTemplate<String, ShowtimePriceResponseEvent> kafkaTemplate;

    public ShowtimePriceKafkaProducer(
            KafkaTemplate<String, ShowtimePriceResponseEvent> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<SendResult<String, ShowtimePriceResponseEvent>> publish(
            UUID bookingId,
            ShowtimePriceResponseEvent event
    ) {

        log.info(
                "Publishing SHOWTIME_PRICE_RESPONSE. bookingId={}, showtimeId={}, price={}",
                bookingId,
                event.getShowtimeId(),
                event.getBasePrice()
        );

        CompletableFuture<SendResult<String, ShowtimePriceResponseEvent>> future =
                kafkaTemplate.send(
                        TOPIC,
                        bookingId.toString(),
                        event
                );

        future.whenComplete((result, ex) -> {

            if (ex == null) {

                log.info(
                        "SHOWTIME_PRICE_RESPONSE published successfully. " +
                                "bookingId={}, topic={}, partition={}, offset={}",
                        bookingId,
                        TOPIC,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()
                );

            } else {

                log.error(
                        "FAILED to publish SHOWTIME_PRICE_RESPONSE. bookingId={}, topic={}",
                        bookingId,
                        TOPIC,
                        ex
                );
            }
        });

        return future;
    }
}