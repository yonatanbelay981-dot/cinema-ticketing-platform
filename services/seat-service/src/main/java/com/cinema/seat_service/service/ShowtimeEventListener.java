package com.cinema.seat_service.service;

import com.cinema.seat_service.entity.ShowTimeCache;
import com.cinema.seat_service.event.ShowTimeEvent;
import com.cinema.seat_service.repository.ShowtimeCacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class ShowtimeEventListener {
 private  final ShowtimeCacheRepository showtimeCacheRepository;

    public ShowtimeEventListener(ShowtimeCacheRepository showtimeCacheRepository) {
        this.showtimeCacheRepository = showtimeCacheRepository;
    }

    @KafkaListener(topics = "showtime-events", groupId = "seat-service-group")
    public void listenShowtimeEvent(ShowTimeEvent event , Acknowledgment ack) {
        try {
            switch (event.getEventType()){
                case SHOWTIME_CREATED:
                case SHOWTIME_UPDATED:
                    showtimeUpsert(event);
                    break;
                case SHOWTIME_DELETED:
                    showtimeDelete(event.getShowtimeId());
                    break;
                default:
                    log.warn("Unknown showtime event type: {}", event.getEventType());
            }
            ack.acknowledge();

        }catch (Exception e) {
            log.error("Failed processing showTime event {}", event.getStartTime(), e);
        }

    }

    public void showtimeUpsert(ShowTimeEvent event){
        var showtimeCache = showtimeCacheRepository.findById(event.getShowtimeId()).orElse(new ShowTimeCache());
        showtimeCache.setShowtimeId(event.getShowtimeId());
        showtimeCache.setMovieId(event.getMovieId());
        showtimeCache.setHallId(event.getHallId());
        showtimeCache.setStartTime(event.getStartTime());
        showtimeCache.setEndTime(event.getEndTime());
        showtimeCacheRepository.save(showtimeCache);
        log.info("Upserted showtime cache for showtimeId: {}", event.getShowtimeId());
    }

    public  void showtimeDelete(UUID showtimeId){
        if(showtimeCacheRepository.existsById(showtimeId)){
            showtimeCacheRepository.deleteById(showtimeId);
            log.info("Deleted showtime cache for showtimeId: {}", showtimeId);
        }else {
            log.warn("Showtime cache for showtimeId: {} does not exist, cannot delete", showtimeId);
        }
    }
}
