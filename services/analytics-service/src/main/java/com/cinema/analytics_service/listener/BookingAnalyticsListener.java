package com.cinema.analytics_service.listener;


import com.cinema.analytics_service.entity.BookingAnalytics;
import com.cinema.analytics_service.repository.BookingAnalyticsRepository;
import com.cinema.common_lib.event.BookingAnalyticsStatus;
import com.cinema.common_lib.event.BookingStatusAnalyticsEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
public class BookingAnalyticsListener {

    private final BookingAnalyticsRepository bookingAnalyticsRepository;

    public BookingAnalyticsListener(
            BookingAnalyticsRepository bookingAnalyticsRepository
    ) {
        this.bookingAnalyticsRepository = bookingAnalyticsRepository;
    }

    @KafkaListener(
            topics = "booking-status-analytics-topic",
            groupId = "analytics-service-group",
            containerFactory = "bookingStatusAnalyticsKafkaListenerContainerFactory"
    )
    public void listenBookingStatusAnalyticsEvent(
            BookingStatusAnalyticsEvent event
    ) {

        log.info(
                "Received booking status analytics event. bookingId={}, status={}",
                event.bookingId(),
                event.status()
        );

        LocalDate today = LocalDate.now();

        BookingAnalytics analytics =
                bookingAnalyticsRepository
                        .findByDate(today)
                        .orElseGet(() -> {

                            BookingAnalytics newAnalytics =
                                    new BookingAnalytics();

                            newAnalytics.setDate(today);
                            newAnalytics.setTotalBookings(0L);
                            newAnalytics.setConfirmedBookings(0L);
                            newAnalytics.setFailedBookings(0L);
                            newAnalytics.setCancelledBookings(0L);

                            return newAnalytics;
                        });

        analytics.setTotalBookings(
                analytics.getTotalBookings() + 1
        );

        if (event.status() == BookingAnalyticsStatus.CONFIRMED) {

            analytics.setConfirmedBookings(
                    analytics.getConfirmedBookings() + 1
            );

        } else if (event.status() == BookingAnalyticsStatus.FAILED) {

            analytics.setFailedBookings(
                    analytics.getFailedBookings() + 1
            );

        } else if (event.status() == BookingAnalyticsStatus.CANCELLED) {

            analytics.setCancelledBookings(
                    analytics.getCancelledBookings() + 1
            );
        }

        bookingAnalyticsRepository.save(analytics);

        log.info(
                "Booking analytics updated. date={}, total={}, confirmed={}, failed={}, cancelled={}",
                analytics.getDate(),
                analytics.getTotalBookings(),
                analytics.getConfirmedBookings(),
                analytics.getFailedBookings(),
                analytics.getCancelledBookings()
        );
    }
}
