package com.cinema.analytics_service.listener;


import com.cinema.analytics_service.entity.ProcessedAnalyticsEvent;
import com.cinema.analytics_service.entity.SalesAnalytics;
import com.cinema.analytics_service.repository.ProcessedAnalyticsEventRepository;
import com.cinema.analytics_service.repository.SalesAnalyticsRepository;
import com.cinema.common_lib.event.BookingAnalyticsEvent;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Slf4j
public class SalesAnalyticsListener {

    private final SalesAnalyticsRepository salesAnalyticsRepository;
    private final ProcessedAnalyticsEventRepository processedAnalyticsEventRepository;

    public SalesAnalyticsListener(
            SalesAnalyticsRepository salesAnalyticsRepository, ProcessedAnalyticsEventRepository processedAnalyticsEventRepository
    ) {
        this.salesAnalyticsRepository = salesAnalyticsRepository;
        this.processedAnalyticsEventRepository = processedAnalyticsEventRepository;
    }
    @Transactional
    @KafkaListener(
            topics = "bokking-analytic-topic",
            groupId = "sales-analytics-service-group",
            containerFactory = "bookingAnalyticsEventKafkaListenerContainerFactory"
    )
    public void listenBookingAnalyticsEvent(
            BookingAnalyticsEvent event
    ) {

        log.info(
                "Received sales analytics event. bookingId={}, ticketCount={}, totalAmount={}",
                event.bookingId(),
                event.ticketCount(),
                event.totalAmount()
        );

        LocalDate today = LocalDate.now();
        if (processedAnalyticsEventRepository .existsByEventIdAndProcessorType(
                event.eventId(),
                "SALE"
        )) {

            log.warn(
                    "Analytics event already processed. eventId={}, bookingId={}",
                    event.eventId(),
                    event.bookingId()
            );

            return;
        }


        SalesAnalytics salesAnalytics =
                salesAnalyticsRepository
                        .findByDate(today)
                        .orElseGet(() -> {

                            SalesAnalytics analytics =
                                    new SalesAnalytics();

                            analytics.setDate(today);
                            analytics.setTotalRevenue(BigDecimal.ZERO);
                            analytics.setTotalTicketsSold(0L);
                            analytics.setTotalBookings(0L);

                            return analytics;
                        });

        salesAnalytics.setTotalRevenue(
                salesAnalytics.getTotalRevenue()
                        .add(event.totalAmount())
        );

        salesAnalytics.setTotalTicketsSold(
                salesAnalytics.getTotalTicketsSold()
                        + event.ticketCount()
        );

        salesAnalytics.setTotalBookings(
                salesAnalytics.getTotalBookings() + 1
        );

        salesAnalyticsRepository.save(salesAnalytics);

        ProcessedAnalyticsEvent processedEvent =
                new ProcessedAnalyticsEvent();

        processedEvent.setEventId(event.eventId());
        processedEvent.setProcessorType("SALE");
        processedEvent.setProcessedAt(LocalDateTime.now());

        processedAnalyticsEventRepository.save(processedEvent);


        log.info(
                "Sales analytics updated. date={}, revenue={}, tickets={}, bookings={}",
                salesAnalytics.getDate(),
                salesAnalytics.getTotalRevenue(),
                salesAnalytics.getTotalTicketsSold(),
                salesAnalytics.getTotalBookings()
        );
    }
}
