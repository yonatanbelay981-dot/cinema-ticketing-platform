package com.cinema.cinema_service.services;

import com.cinema.cinema_service.dto.CinemaResponse;
import com.cinema.cinema_service.dto.CreateCinemaRequest;
import com.cinema.cinema_service.dto.UpdateCinemaRequest;
import com.cinema.cinema_service.entity.Cinema;
import com.cinema.cinema_service.repository.CinemaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
@Slf4j
public class CinemaServiceImplementation implements CinemaService {
    private final CinemaRepository cinemaRepository;

    public CinemaServiceImplementation(CinemaRepository cinemaRepository) {
        this.cinemaRepository = cinemaRepository;
    }


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
            return new RuntimeException("Cinema not found with id " + id);
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
        return mapTOCinemaResponse(savedCinema);
    }

    @Override
    public CinemaResponse updateCinema(UUID id, UpdateCinemaRequest request) {
        log.info("Updating cinema with id {}"  , id);
        Cinema cinema = cinemaRepository.findById(id).orElseThrow(()->{
            log.warn("cinema not found with id {}"  , id);
            return new RuntimeException("Cinema not found with id " + id);
        });
        cinema.setName(request.getName());
        cinema.setAddress(request.getAddress());
        cinema.setPhone(request.getPhone());

        Cinema savedCinema   =  cinemaRepository.save(cinema);
        log.info("cinema update is successful with id {}"  , id);
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
            return new RuntimeException("cinema was not found with id " + id);
        });
        cinemaRepository.delete(cinema);
        log.info("cinema with {} id was deleted successfully" , id);

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
