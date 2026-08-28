package com.cinema.analytics_service.listener;

import com.cinema.analytics_service.entity.MovieAnalytics;
import com.cinema.analytics_service.entity.ProcessedAnalyticsEvent;
import com.cinema.analytics_service.repository.MovieAnalyticsRepository;
import com.cinema.analytics_service.repository.ProcessedAnalyticsEventRepository;
import com.cinema.common_lib.event.BookingAnalyticsEvent;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
public class MovieAnalyticsListener {
    private final MovieAnalyticsRepository movieAnalyticsRepository;
   private final ProcessedAnalyticsEventRepository processedAnalyticsEventRepository;

    public MovieAnalyticsListener(MovieAnalyticsRepository movieAnalyticsRepository,   ProcessedAnalyticsEventRepository processedAnalyticsEventRepository) {
        this.movieAnalyticsRepository = movieAnalyticsRepository;
        this.processedAnalyticsEventRepository = processedAnalyticsEventRepository;

    }
    @Transactional
    @KafkaListener(
            topics = "bokking-analytic-topic",
            groupId = "analytic-service-group",
            containerFactory = "bookingAnalyticsEventKafkaListenerContainerFactory"
    )
    public void listenBookingAnalyticsEvent(BookingAnalyticsEvent event){
        log.info( "Received booking analytics event. bookingId={}, movieId={}, ticketCount={}, totalAmount={}",
                event.bookingId(),
                event.movieId(),
                event.ticketCount(),
                event.totalAmount() );

        if (processedAnalyticsEventRepository .existsByEventIdAndProcessorType(
                event.eventId(),
                "MOVIE"
        )) {

            log.warn(
                    "Analytics event already processed. eventId={}, bookingId={}",
                    event.eventId(),
                    event.bookingId()
            );

            return;
        }

        MovieAnalytics movieAnalytics = movieAnalyticsRepository.findByMovieId(event.movieId()).orElseGet(()->{
            MovieAnalytics newAnalytics =  new MovieAnalytics();
            newAnalytics.setMovieId(event.movieId());
            newAnalytics.setTotalBookings(0L);
            newAnalytics.setTicketsSold(0L);
            newAnalytics.setTotalRevenue(BigDecimal.ZERO);
            return newAnalytics;
        });


        movieAnalytics.setTotalBookings(
                movieAnalytics.getTotalBookings() + 1
        );

        movieAnalytics.setTicketsSold(
                movieAnalytics.getTicketsSold() + event.ticketCount()
        );

        movieAnalytics.setTotalRevenue(
                movieAnalytics.getTotalRevenue() .add(event.totalAmount())
        );

        movieAnalyticsRepository.save(movieAnalytics);

        ProcessedAnalyticsEvent processedEvent =
                new ProcessedAnalyticsEvent();

        processedEvent.setEventId(event.eventId());
        processedEvent.setProcessorType("MOVIE");
        processedEvent.setProcessedAt(LocalDateTime.now());

        processedAnalyticsEventRepository.save(processedEvent);

        log.info( "Movie analytics updated successfully. movieId={}, totalBookings={}, totalTickets={}, totalRevenue={}",
                movieAnalytics.getMovieId(),
                movieAnalytics.getTotalBookings(),
                movieAnalytics.getTicketsSold(),
                movieAnalytics.getTotalRevenue()
        );



    }
}
