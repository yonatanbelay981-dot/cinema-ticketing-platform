package com.cinema.cinema_service.services;

import com.cinema.cinema_service.dto.CreateHallRequest;
import com.cinema.cinema_service.dto.HallResponse;
import com.cinema.cinema_service.dto.UpdateHallRequest;
import com.cinema.cinema_service.entity.Cinema;
import com.cinema.cinema_service.entity.Hall;
import com.cinema.cinema_service.event.HallEvent;
import com.cinema.cinema_service.exception.CinemaNotFoundException;
import com.cinema.cinema_service.exception.HallNotFoundException;
import com.cinema.cinema_service.repository.CinemaRepository;
import com.cinema.cinema_service.repository.HallRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class HallServiceImplementation implements HallServices {

    private final HallRepository hallRepository;
    private final CinemaRepository cinemaRepository;
    private  final KafkaHallProducerService kafkaHallProducerService;

    public HallServiceImplementation(HallRepository hallRepository, CinemaRepository cinemaRepository, KafkaHallProducerService kafkaHallProducerService) {
        this.hallRepository = hallRepository;
        this.cinemaRepository = cinemaRepository;
        this.kafkaHallProducerService = kafkaHallProducerService;
    }


    @Override
    public Page<HallResponse> getAllHalls(Pageable pageable) {
        log.info("Fetching Halls...");
        Page<Hall> halls = hallRepository.findAll(pageable);
        log.info("Fetched halls successfully");
        return halls.map(this::mapToHallResponse);

    }

    @Override
    public HallResponse createHall(CreateHallRequest request) {
        log.info("creating hall with the name {}"  , request.getName());
        Hall hall =  new Hall();
        hall.setName(request.getName());
        hall.setCapacity(request.getCapacity());
        Cinema cinema  =  cinemaRepository.findById(request.getCinemaId()).orElseThrow(()->{
            log.warn("cinema with the id {} was not found"  , request.getCinemaId());
            return new CinemaNotFoundException("cinema  was not found with id " + request.getCinemaId());
        });
        hall.setCinema(cinema);
        Hall savedHall = hallRepository.save(hall);
        log.info("created hall with name {}   successfully"  , request.getName());

        CompletableFuture<SendResult<String, HallEvent>> future = kafkaHallProducerService.publish(
                 new HallEvent(

                HallEvent.EventType.HALL_CREATED,
                savedHall.getId(),
                savedHall.getName(),
                savedHall.getCapacity()

        ));

        future.thenAccept(result->{
            log.info("Published {} event for hall {} at offset {}",
                    HallEvent.EventType.HALL_CREATED,
                    savedHall.getId().toString(),
                    result.getRecordMetadata().offset()
            );
        }).exceptionally(ex-> {
            log.error("failed publishing for created event for {} hall",
                    savedHall.getId().toString(), ex);
            return null;
        });

        return mapToHallResponse(savedHall);
    }

    @Override
    public HallResponse updateHall(UUID id, UpdateHallRequest request) {
        log.info("updating hall with id {} " , id);
        Hall hall = hallRepository.findById(id).orElseThrow(()->{
            log.warn("hall was not found with id {} " , id);
            return new HallNotFoundException("hall not found with id " + id);
        });
        hall.setName(request.getName());
        hall.setCapacity(request.getCapacity());
        Hall savedHall = hallRepository.save(hall);
        log.info("update was successful with  id {}" , id);

        CompletableFuture<SendResult<String, HallEvent>> future = kafkaHallProducerService.publish(new HallEvent(
                HallEvent.EventType.HALL_UPDATED,
                savedHall.getId(),
                savedHall.getName(),
                savedHall.getCapacity()
        ));

        future.thenAccept(result->{
            log.info("Published {} events for hall {} at offset {}", HallEvent.EventType.HALL_UPDATED, savedHall.getId(), result.getRecordMetadata().offset());
        }).exceptionally(ex-> {
            log.error("failed publishing {} event for {} hall", HallEvent.EventType.HALL_UPDATED, savedHall.getId(), ex);
            return null;
        });

        return mapToHallResponse(savedHall);

    }

    @Override
    public Page<HallResponse> searchByName(String name, Pageable pageable) {
        log.info("searching halls with name {}"  ,  name);
        Page<Hall> hall =  hallRepository.findByNameContainingIgnoreCase(name , pageable);
        log.info("Found  {} halls by name {}" , hall.getTotalElements() , name);
        return hall.map(this::mapToHallResponse);
    }

    @Override
    public HallResponse getHallById(UUID id) {
        log.info("Fetching halls by its id {} "  , id);
        Hall hall =  hallRepository.findById(id).orElseThrow(()->{
            log.warn("halls with the id {} was not found" , id);
            return  new HallNotFoundException("halls was not found");
                }

        );
        log.info("Found  halls with the id {} " , id);
        return mapToHallResponse(hall);
    }

    @Override
    public void deleteHallById(UUID id) {
        log.info("deleting hall with id  {}"  , id);
        Hall hall =  hallRepository.findById(id).orElseThrow(()->{
            log.warn("to Delete halls with the id {} was not found" , id);
            return  new HallNotFoundException("halls was not found");
        });

        hallRepository.delete(hall);
        log.info("deleting hall with id {} was successful " , id);

        CompletableFuture<SendResult<String, HallEvent>> future = kafkaHallProducerService.publish(new HallEvent(
                HallEvent.EventType.HALL_DELETED,
                hall.getId(),
                hall.getName(),
                hall.getCapacity()
        ));
        future.thenAccept(result->{
            log.info("published deleted  event for {} hall at {} offset" ,
                     hall.getId().toString(), result.getRecordMetadata().offset());
        }).exceptionally(ex->{
            log.error("failed publishing {} event for {} hall" , HallEvent.EventType.HALL_DELETED, hall.getId().toString(), ex);
            return null;
        });


    }
    private HallResponse mapToHallResponse(Hall hall){
        HallResponse response =  new HallResponse();
        response.setId(hall.getId());
        response.setName(hall.getName());
        response.setCapacity(hall.getCapacity());
        response.setCinemaId(hall.getCinema().getId());

        return  response;

    }
}
