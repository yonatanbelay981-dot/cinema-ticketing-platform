package com.cinema.cinema_service.services;

import com.cinema.cinema_service.dto.CinemaResponse;
import com.cinema.cinema_service.dto.CreateCinemaRequest;
import com.cinema.cinema_service.dto.UpdateCinemaRequest;
import com.cinema.cinema_service.entity.Cinema;
import com.cinema.cinema_service.event.CinemaEvent;
import com.cinema.cinema_service.exception.CinemaNotFoundException;
import com.cinema.cinema_service.repository.CinemaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class CinemaServiceImplementation implements CinemaService {
    private final CinemaRepository cinemaRepository;

    public CinemaServiceImplementation(CinemaRepository cinemaRepository, KafkaTemplate<String, CinemaEvent> kafkaTemplate, KafkaProducerService kafkaProducerService) {
        this.cinemaRepository = cinemaRepository;
        this.kafkaProducerService = kafkaProducerService;

    }

    private final KafkaProducerService kafkaProducerService;



    @Override
    public Page<CinemaResponse> getAllCinemas(Pageable pageable) {
        log.info("Fetching all cinemas");
       Page<Cinema> cinemas = cinemaRepository.findAll(pageable);
       log.info("Found  {} cinemas"  , cinemas.getTotalElements());
       return  cinemas.map(this::mapTOCinemaResponse);
    }


    @Override
    public CinemaResponse getCinemaById(UUID id) {
        log.info("find cinema with id {} "  , id);
        Cinema cinemas =  cinemaRepository.findById(id).orElseThrow(()->{
            log.warn("Cinema with id {} not found"  , id);
            return new CinemaNotFoundException("Cinema not found with id " + id);
        });
       log.info("Cinema with id {} found"  , id);

        return mapTOCinemaResponse(cinemas);
    }

    @Override
    public CinemaResponse createCinema(CreateCinemaRequest request) {
        log.info("Creating cinema with name {}"  , request.getName());
        Cinema cinema = new Cinema();
        cinema.setName(request.getName());
        cinema.setAddress(request.getAddress());
        cinema.setPhone(request.getPhone());
        Cinema savedCinema = cinemaRepository.save(cinema);
        log.info("Cinema successfully created with id {} "  , savedCinema.getId());
        CompletableFuture<SendResult<String , CinemaEvent>> future = kafkaProducerService.publish(
                new CinemaEvent(
                        CinemaEvent.EventType.CINEMA_CREATED,
                        savedCinema.getId(),
                        savedCinema.getName(),
                        savedCinema.getAddress()


                )
        );
        future.thenAccept(result->{
            log.info("Published CREATED event for cinema {} at offset {}",
                    savedCinema.getId(),
                    result.getRecordMetadata().offset());
        }).exceptionally(ex->{

            log.error(
                    "Failed publishing CREATED event for CINEMA {}",
                    savedCinema.getId(),
                    ex
            );
            return  null;
        }
        );
        return mapTOCinemaResponse(savedCinema);
    }

    @Override
    public CinemaResponse updateCinema(UUID id, UpdateCinemaRequest request) {
        log.info("Updating cinema with id {}"  , id);
        Cinema cinema = cinemaRepository.findById(id).orElseThrow(()->{
            log.warn("cinema not found with id {}"  , id);
            return new CinemaNotFoundException("Cinema not found with id " + id);
        });
        cinema.setName(request.getName());
        cinema.setAddress(request.getAddress());
        cinema.setPhone(request.getPhone());

        Cinema savedCinema   =  cinemaRepository.save(cinema);
        log.info("cinema update is successful with id {}"  , id);
        CompletableFuture<SendResult<String , CinemaEvent>> future = kafkaProducerService.publish(
                new CinemaEvent(
                        CinemaEvent.EventType.CINEMA_UPDATED,
                        savedCinema.getId(),
                        savedCinema.getName(),
                        savedCinema.getAddress()
                )
        );
        future.thenAccept(result->{
            log.info("Published UPDATED event for cinema {} at offset {}",
                    savedCinema.getId(),
                    result.getRecordMetadata().offset());
        }).exceptionally(ex->{
            log.error(
                    "Failed publishing UPDATED event for CINEMA {}",
                    savedCinema.getId(),
                    ex
            );
            return  null;
        }
        );
        
        return mapTOCinemaResponse(savedCinema);
    }

    @Override
    public  Page<CinemaResponse> searchCinemaByName(String name  , Pageable pageable) {
       log.info("searching cinema by name {}"  , name);
      Page<Cinema> cinema =  cinemaRepository.findByNameContainingIgnoreCase(name , pageable);
       log.info("cinema is successfully found with name {}"  , name);
       return cinema.map(this::mapTOCinemaResponse);
    }

    @Override
    public Page<CinemaResponse> searchCinemaByAddress(String address  , Pageable pageable) {
        log.info("searching cinema by address {}"  , address);
        Page<Cinema> cinema  = cinemaRepository.findByAddressContainingIgnoreCase(address ,  pageable);
        log.info("cinema is successfully found with address {}"  , address);
        return cinema.map(this::mapTOCinemaResponse);
    }

    @Override
    public void deleteCinemaById(UUID id) {
        log.info("Deleting  cinema with id {}" , id);
        Cinema cinema = cinemaRepository.findById(id).orElseThrow(()->{
            log.warn("cinema was not found with id  {} " , id);
            return new CinemaNotFoundException("cinema was not found with id " + id);
        });
        cinemaRepository.delete(cinema);
        log.info("cinema with {} id was deleted successfully" , id);
        CompletableFuture<SendResult<String , CinemaEvent>> future = kafkaProducerService.publish(
                new CinemaEvent(
                        CinemaEvent.EventType.CINEMA_DELETED,
                        cinema.getId(),
                        cinema.getName(),
                        cinema.getAddress()
                )
        );

        future.thenAccept(result->{
            log.info("Published DELETED event for cinema {} at offset {}",
                    cinema.getId(),
                    result.getRecordMetadata().offset());
        }).exceptionally(ex->{
            log.error(
                    "Failed publishing DELETED event for CINEMA {}",
                    cinema.getId(),
                    ex
            );
            return  null;
        }
        );

    }

    private CinemaResponse mapTOCinemaResponse(Cinema cinema) {
        CinemaResponse response =  new CinemaResponse();
        response.setId(cinema.getId());
        response.setName(cinema.getName());
        response.setAddress(cinema.getAddress());
        response.setPhone(cinema.getPhone());
        return response;
    }
}
