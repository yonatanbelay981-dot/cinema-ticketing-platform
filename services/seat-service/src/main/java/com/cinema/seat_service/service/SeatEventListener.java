package com.cinema.seat_service.service;

import com.cinema.seat_service.entity.HallCache;
import com.cinema.seat_service.entity.Seat;
import com.cinema.seat_service.entity.SeatType;
import com.cinema.seat_service.event.HallEvent;
import com.cinema.seat_service.repository.HallCacheRepository;
import com.cinema.seat_service.repository.SeatRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SeatEventListener {

    private final HallCacheRepository hallCacheRepository;
    private final SeatRepository seatRepository;

    public SeatEventListener(HallCacheRepository hallCacheRepository, SeatRepository seatRepository) {
        this.hallCacheRepository = hallCacheRepository;
        this.seatRepository = seatRepository;
    }

    @Transactional
    @KafkaListener
            (topics = "hall-availability-events"  ,
            groupId = "seat-service-group" ,
            containerFactory = "hallEventConcurrentKafkaListenerContainerFactory" )

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
            throw e;
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


        List<Seat> existingSeats =
                seatRepository.findByHallId(event.getHallId());


        Set<String> existingSeatPositions =
                existingSeats.stream()
                        .map(seat ->
                                seat.getRowName()
                                        + "-"
                                        + seat.getSeatNumber()
                        )
                        .collect(Collectors.toSet());

        List<Seat> seats = new ArrayList<>();

        for (int i = 0; i < totalSeats; i++) {

            String rowName =
                    String.valueOf((char) ('A' + (i / seatsPerRow)));

            int seatNumber =
                    (i % seatsPerRow) + 1;

            String seatPosition =
                    rowName + "-" + seatNumber;


            if (existingSeatPositions.contains(seatPosition)) {
                continue;
            }

            Seat seat = new Seat();

            seat.setHallId(event.getHallId());
            seat.setRowName(rowName);
            seat.setSeatNumber(seatNumber);
            seat.setSeatType(SeatType.REGULAR);

            seats.add(seat);
        }

        if (!seats.isEmpty()) {
            seatRepository.saveAll(seats);
        }

        log.info(
                "Generated {} new seats for hall {}",
                seats.size(),
                event.getHallId()
        );
    }
}
