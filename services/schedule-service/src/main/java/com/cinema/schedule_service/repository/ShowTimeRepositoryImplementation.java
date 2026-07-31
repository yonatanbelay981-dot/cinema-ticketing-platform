package com.cinema.schedule_service.repository;

import com.cinema.schedule_service.dto.CreateShowtimeRequest;
import com.cinema.schedule_service.dto.ShowtimeResponse;
import com.cinema.schedule_service.dto.UpdateShowtimeRequest;
import com.cinema.schedule_service.entity.ShowTime;
import com.cinema.schedule_service.exception.ShowTimeNotFoundException;
import com.cinema.schedule_service.service.ShowTimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class ShowTimeRepositoryImplementation implements ShowTimeService {

    private final ShowTimeRepository showTimeRepository;

    public ShowTimeRepositoryImplementation(ShowTimeRepository showTimeRepository) {
        this.showTimeRepository = showTimeRepository;
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
        log.info("creating showTime with movieId {} and hallId{} " , request.getHallId() , request.getMovieId());
        ShowTime showTime = new ShowTime();
        showTime.setMovieId(request.getMovieId());
        showTime.setHallId(request.getHallId());
        showTime.setStartTime(request.getStartTime());
        showTime.setEndTime(request.getEndTime());
        ShowTime savedShowTime = showTimeRepository.save(showTime);
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
        showTime.setMovieId(request.getMovieId());
        showTime.setHallId(request.getHallId());
        showTime.setStartTime(request.getStartTime());
        showTime.setEndTime(request.getEndTime());
        ShowTime savedShowTime = showTimeRepository.save(showTime);
        log.info("showTime updated with id {}" , savedShowTime.getId());
        return convertToResponse(savedShowTime);
    }

    @Override
    public void deleteShowTimeById(UUID id) {
        log.info("deleting showTime with id {}" , id);
        showTimeRepository.deleteById(id);
        log.info("showTime deleted with id {}" , id);
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
        return response;
    }
}
