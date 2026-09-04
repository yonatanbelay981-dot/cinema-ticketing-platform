package com.cinema.schedule_service.service;

import com.cinema.schedule_service.dto.CreateShowtimeRequest;
import com.cinema.schedule_service.dto.ShowtimeResponse;
import com.cinema.schedule_service.dto.UpdateShowtimeRequest;
import com.cinema.schedule_service.entity.ScheduleStatus;
import com.cinema.schedule_service.entity.ShowTime;
import com.cinema.schedule_service.event.ShowtimeEvent;
import com.cinema.schedule_service.exception.ShowTimeNotFoundException;
import com.cinema.schedule_service.repository.HallCacheRepository;
import com.cinema.schedule_service.repository.MovieCacheRepository;
import com.cinema.schedule_service.repository.ShowTimeRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ShowTimeServiceImplementation implements ShowTimeService {

    private final ShowTimeRepository showTimeRepository;
    private final MovieCacheRepository movieCacheRepository;
    private final HallCacheRepository hallCacheRepository;
    private final KafkaProducerService kafkaProducerService;

    public ShowTimeServiceImplementation(ShowTimeRepository showTimeRepository, MovieCacheRepository movieCacheRepository, HallCacheRepository hallCacheRepository, KafkaProducerService kafkaProducerService) {
        this.showTimeRepository = showTimeRepository;
        this.movieCacheRepository = movieCacheRepository;
        this.hallCacheRepository = hallCacheRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Override
    public Page<ShowtimeResponse> getAllSHowTime(Pageable pageable) {

       log.info("fetching showTimes");
       Page<ShowTime> showtime = showTimeRepository.findAll(pageable);
       log.info("fetched {} showTimes" , showtime.getTotalElements());
       return showtime.map(this::convertToResponse);

    }

    @Override
    public ShowtimeResponse getShowTimeById(UUID id) {
        log.info("fetching showTime with id {}" , id);
        ShowTime showTime = showTimeRepository.findById(id).orElseThrow(()->{
            log.warn("showTime with id {} not found" , id);
            return new ShowTimeNotFoundException("showTime not found with id " + id);
        });
        log.info("fetched showTime with id {}" , id);
        return convertToResponse(showTime);
    }

    @Override
    public ShowtimeResponse createShowTime(CreateShowtimeRequest request) {

        log.info("creating showTime with movieId {} and hallId{} " , request.getMovieId() , request.getHallId());
        movieCacheRepository.findById(request.getMovieId()).orElseThrow(()->{
            log.warn("movie with id {} not found" , request.getMovieId());
            return new IllegalArgumentException("movie not found with id " + request.getMovieId());
        });

         hallCacheRepository.findById(request.getHallId()).orElseThrow(()->{
            log.warn("hall with id {} not found" , request.getHallId());
            return new IllegalArgumentException("hall not found with id " + request.getHallId());
        });

        ShowTime showTime = new ShowTime();
        showTime.setMovieId(request.getMovieId());
        showTime.setHallId(request.getHallId());
        showTime.setStartTime(request.getStartTime());
        showTime.setEndTime(request.getEndTime());
        showTime.setBasePrice(request.getBasePrice());
        showTime.setStatus((ScheduleStatus.SCHEDULED));

        if(!request.getEndTime().isAfter(request.getStartTime())){
            log.warn("endTime {} is not after startTime {}" , request.getEndTime() , request.getStartTime());
            throw new IllegalArgumentException("endTime " + request.getEndTime() + " is not after startTime " + request.getStartTime());
        }

        if (showTimeRepository.existsByHallIdAndStartTimeLessThanAndEndTimeGreaterThan(
                request.getHallId(),
                request.getEndTime(),
                request.getStartTime()

        )) {
            log.warn("showTime with hallId {} and startTime {} and endTime {} already exists" , request.getHallId() , request.getStartTime() , request.getEndTime());
            throw new IllegalArgumentException("showTime with hallId " + request.getHallId() + " and startTime " + request.getStartTime() + " and endTime " + request.getEndTime() + " already exists");
        }



        ShowTime savedShowTime = showTimeRepository.save(showTime);
        CompletableFuture<SendResult<String , ShowtimeEvent>> future = kafkaProducerService.publish(
                new ShowtimeEvent(
                                   ShowtimeEvent.EventType.SHOWTIME_CREATED,
                                   savedShowTime.getId() ,
                                   savedShowTime.getMovieId(),
                                   savedShowTime.getHallId(),
                                   savedShowTime.getStartTime(),
                                   savedShowTime.getEndTime()));
                future.thenAccept(result->{

                                       log.info("published created event for a {} showtime at {} offset" , savedShowTime.getId()  , result.getRecordMetadata().offset() );

        }).exceptionally(ex->{

            log.warn("failed publishing  created event for a {} showtime " , savedShowTime.getId()  ,  ex );

            return null;

        }



        );

        log.info("showTime created with id {}" , savedShowTime.getId());
        return convertToResponse(savedShowTime);
    }

    @Override
    public ShowtimeResponse updateShowTime(UUID id, UpdateShowtimeRequest request) {
        log.info("updating showTime with id {}" , id);

        ShowTime showTime = showTimeRepository.findById(id).orElseThrow(()->{
            log.warn("while updating showTime with id {} not found" , id);
            return new ShowTimeNotFoundException("showTime not found with id " + id);
        });

        showTime.setStartTime(request.getStartTime());
        showTime.setEndTime(request.getEndTime());
        showTime.setBasePrice(request.getBasePrice());
        showTime.setStatus(request.getStatus());

        if(!request.getEndTime().isAfter(request.getStartTime())){
            log.warn("while updating the endTime {} is not after startTime {}" , request.getEndTime() , request.getStartTime());
            throw new IllegalArgumentException("endTime " + request.getEndTime() + " is not after startTime " + request.getStartTime());
        }
        if (showTimeRepository.existsByHallIdAndStartTimeLessThanAndEndTimeGreaterThanAndIdNot(
                showTime.getHallId(),
                request.getEndTime(),
                request.getStartTime(),
                showTime.getId()
        )) {
            log.warn("showTime of startTime {} and endTime {} already exists"  , request.getStartTime() , request.getEndTime());
            throw new IllegalArgumentException("showTime with hall  startTime " + request.getStartTime() + " and endTime " + request.getEndTime() + " already exists");
        }

        ShowTime savedShowTime = showTimeRepository.save(showTime);
        log.info("showTime updated with id {}" , savedShowTime.getId());

        CompletableFuture<SendResult<String , ShowtimeEvent>> future = kafkaProducerService.publish(
                new ShowtimeEvent(
                        ShowtimeEvent.EventType.SHOWTIME_UPDATED,
                        savedShowTime.getId(),
                        savedShowTime.getMovieId(),
                        savedShowTime.getHallId(),
                        savedShowTime.getStartTime(),
                        savedShowTime.getEndTime()
                )
        );
        future.thenAccept(result->{
            log.info("published updated event for a {} showtime  at {} offset"  ,  savedShowTime.getId()  ,  result.getRecordMetadata().offset());
        }).exceptionally(ex->{
            log.warn(
                    "failed publishing  updated event for a {} showtime  "  ,  savedShowTime.getId()  ,  ex
            );
            return  null;
        }
        );
        return convertToResponse(savedShowTime);
    }

    @Override
    public void deleteShowTimeById(UUID id) {
        log.info("deleting showTime with id {}" , id);
        ShowTime showTime = showTimeRepository.findById(id)
                .orElseThrow(() ->
                        new ShowTimeNotFoundException(
                                "ShowTime not found with id " + id
                        )
                );

        showTimeRepository.delete(showTime);
        log.info("showTime deleted with id {}" , id);

        CompletableFuture<SendResult<String , ShowtimeEvent>> future = kafkaProducerService.publish(
                new ShowtimeEvent(
                        ShowtimeEvent.EventType.SHOWTIME_DELETED,
                        showTime.getId(),
                        showTime.getMovieId(),
                        showTime.getHallId(),
                        showTime.getStartTime(),
                        showTime.getEndTime()
                )
        );
        future.thenAccept(result->{
            log.info("published deleted event for a {} showtime  at {} offset"  ,  showTime.getId()  ,  result.getRecordMetadata().offset());
        }).exceptionally(ex->{
                    log.warn(
                            "failed publishing  deleted event for a {} showtime  "  ,  showTime.getId()  ,  ex
                    );
                    return  null;
                }
        );
    }

    @Override
    public Page<ShowtimeResponse> searchByMovieId(UUID movieId, Pageable pageable) {
        log.info("searching ShowTime by movieId {}" , movieId);
        Page<ShowTime> showtime = showTimeRepository.findByMovieId(movieId , pageable);
        log.info("found {} showTimes by movieId {}" , showtime.getTotalElements() , movieId);
        return showtime.map(this::convertToResponse);
    }

    @Override
    public Page<ShowtimeResponse> searchByHallId(UUID hallId, Pageable pageable) {
        log.info("searching ShowTime by hallId {}" , hallId);
        Page<ShowTime> showtime = showTimeRepository.findByHallId(hallId , pageable);
        log.info("found {} showTimes by hallId {}" , showtime.getTotalElements() , hallId);
        return showtime.map(this::convertToResponse);
    }

    @Override
    public Page<ShowtimeResponse> searchByStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        log.info("searching ShowTime by start time between {} and {}" , startTime , endTime);
        Page<ShowTime> showtime = showTimeRepository.findByStartTimeBetween(startTime , endTime , pageable);
        log.info("found {} showTimes by start time between {} and {}" , showtime.getTotalElements() , startTime , endTime);
        return showtime.map(this::convertToResponse);
    }


    private ShowtimeResponse convertToResponse(ShowTime showTime) {
        ShowtimeResponse response =  new ShowtimeResponse();
        response.setId(showTime.getId());
        response.setMovieId(showTime.getMovieId());
        response.setHallId(showTime.getHallId());
        response.setStartTime(showTime.getStartTime());
        response.setEndTime(showTime.getEndTime());
        response.setBasePrice(showTime.getBasePrice());
        response.setStatus(showTime.getStatus());
        return response;
    }
}
