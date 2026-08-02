package com.cinema.seat_service.service;

import com.cinema.seat_service.entity.HallCache;
import com.cinema.seat_service.entity.Seat;
import com.cinema.seat_service.entity.SeatType;
import com.cinema.seat_service.event.HallEvent;
import com.cinema.seat_service.repository.HallCacheRepository;
import com.cinema.seat_service.repository.SeatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Service
@Slf4j
public class SeatEventListener {

    private final HallCacheRepository hallCacheRepository;
    private final SeatRepository seatRepository;

    public SeatEventListener(HallCacheRepository hallCacheRepository, SeatRepository seatRepository) {
        this.hallCacheRepository = hallCacheRepository;
        this.seatRepository = seatRepository;
    }


    @KafkaListener(topics = "hall-events"  , groupId = "seat-service-group")
    public void listenHallEvent(HallEvent event , Acknowledgment ack) {
        try {


            switch (event.getEventType()) {
                case HALL_CREATED:
                case HALL_UPDATED:
                    handleUpSert(event);
                    generateSeats(event);
                    break;
                case HALL_DELETED:
                    handleDeletedHall(event.getHallId());
                    break;
                default:
                    log.warn("Unknown hall event type: {}", event.getEventType());
            }
            ack.acknowledge();

        } catch (Exception e) {
            log.error("Failed processing hall event {}", event.getHallId(), e);
        }
    }

    private void handleUpSert(HallEvent event){
        HallCache hall  = hallCacheRepository.findById(event.getHallId()).orElse(new HallCache());
        hall.setHallId(event.getHallId());
        hall.setName(event.getName());
        hall.setCapacity(event.getCapacity());
        hallCacheRepository.save(hall);
        log.info("Upserted hall cache for hallId: {}", event.getHallId());

    }

    private void  handleDeletedHall(UUID hallId){
        if (hallCacheRepository.existsById(hallId)) {
            seatRepository.deleteByHallId(hallId);
            hallCacheRepository.deleteById(hallId);

            log.info("Deleted hall cache {}", hallId);

        } else {

            log.warn("Hall {} not found in cache", hallId);

        }

    }

    private void generateSeats(HallEvent event) {

        int seatsPerRow = 10;
        int totalSeats = event.getCapacity();

        for (int i = 0; i < totalSeats; i++) {

            String rowName = String.valueOf((char) ('A' + (i / seatsPerRow)));
            int seatNumber = (i % seatsPerRow) + 1;

            if (seatRepository.existsByHallIdAndRowNameAndSeatNumber(
                    event.getHallId(),
                    rowName,
                    seatNumber)) {
                continue;
            }

            List<Seat> seats = new ArrayList<>();

            Seat seat = new Seat();
            seat.setHallId(event.getHallId());
            seat.setRowName(rowName);
            seat.setSeatNumber(seatNumber);
            seat.setSeatType(SeatType.REGULAR);
            seats.add(seat);

            seatRepository.saveAll(seats);
        }

        log.info("Generated {} seats for hall {}", totalSeats, event.getHallId());
    }
}
