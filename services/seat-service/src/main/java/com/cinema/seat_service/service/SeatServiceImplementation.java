package com.cinema.seat_service.service;

import com.cinema.seat_service.dto.*;
import com.cinema.seat_service.entity.ReservationStatus;
import com.cinema.seat_service.entity.Seat;
import com.cinema.seat_service.entity.SeatReservation;
import com.cinema.seat_service.exception.SeatNotFoundException;
import com.cinema.seat_service.exception.SeatNotHallException;
import com.cinema.seat_service.exception.SeatNotShowTimeException;
import com.cinema.seat_service.exception.SeatUnavailableException;
import com.cinema.seat_service.repository.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class SeatServiceImplementation implements SeatService {

    private final SeatRepository seatRepository;
    private final SeatReservationRepository seatReservationRepository;
    private final HallCacheRepository hallCacheRepository;
    private final ShowtimeCacheRepository showtimeCacheRepository;


    public SeatServiceImplementation(SeatRepository seatRepository, SeatReservationRepository seatReservationRepository, HallCacheRepository hallCacheRepository, ShowtimeCacheRepository showtimeCacheRepository) {
        this.seatRepository = seatRepository;
        this.seatReservationRepository = seatReservationRepository;
        this.hallCacheRepository = hallCacheRepository;
        this.showtimeCacheRepository = showtimeCacheRepository;


    }

    @Override
    public Page<SeatResponse> getAllSeatsByHallId(UUID hallId, Pageable pageable) {
        log.info("Fetching seats for hall with id {}" , hallId);
        Page<Seat> seats = seatRepository.findByHallId(hallId, pageable);
        log.info("Fetched seats for hall with id {} successfully" , hallId);
        return seats.map(this::convertToResponse);

    }

    @Override
    public Optional<SeatResponse> getByHallIdRowNameAndSeatNumber(UUID hallId, String rowName, Integer seatNumber) {
        log.info("Fetching seat for hallId {}, rowName {}, seatNumber {}" , hallId, rowName, seatNumber);
        Optional<Seat> seatOptional = seatRepository.findByHallIdAndRowNameAndSeatNumber(hallId, rowName, seatNumber);
        if (seatOptional.isPresent()) {
            log.info("Fetched seat for hallId {}, rowName {}, seatNumber {} successfully" , hallId, rowName, seatNumber);
            return Optional.of(convertToResponse(seatOptional.get()));
        } else {
            log.warn("Seat for hallId {}, rowName {}, seatNumber {} was not found" , hallId, rowName, seatNumber);
            return Optional.empty();
        }
    }

    @Override
    public SeatResponse getSeatsById(UUID id) {
        log.info("Fetching seat with id {}" , id);
        Seat seat = seatRepository.findById(id).orElseThrow(() -> {
            log.warn("Seat with id {} was not found" , id);
            return new SeatNotFoundException("Seat not found with id " + id);
        });
        log.info("Fetched seat with id {} successfully" , id);
        return convertToResponse(seat);
    }

    @Override
    public SeatResponse updateSeat(UUID id, UpdateSeatRequest request) {
        log.info("Updating seat with id {}" , id);
        Seat seat = seatRepository.findById(id).orElseThrow(() -> {
            log.warn("while updating Seat with id {} was not found" , id);
            return new SeatNotFoundException("Seat not found with id " + id);
        });
        // Update seat properties based on the request
       seat.setSeatType(request.getSeatType());
        Seat updatedSeat = seatRepository.save(seat);
        log.info("Updated seat with id {} successfully" , id);
        return convertToResponse(updatedSeat);

    }

    @Override
    public void deleteSeatById(UUID id) {
        log.info("Deleting seat with id {}" , id);
       Seat seat =  seatRepository.findById(id).orElseThrow(()->{
           log.warn("seat was not found with id {} " , id);
           return new SeatNotFoundException("seat was not found with id {} " + id);
       });
       seatRepository.delete(seat);
        log.info("Deleted seat with id {} successfully" , id);
    }

    @Override
    public List<ShowtimeSeatResponse>  getSeatMapForShowtime(UUID hallId, UUID showtimeId){
        log.info("Fetching dynamic seat map for hallId {} and showtimeId {}", hallId, showtimeId);
        List<Seat> physicalSeats = seatRepository.findByHallId(hallId, Pageable.unpaged()).getContent();
        List<SeatReservation> activeReservations  =  seatReservationRepository.findByShowtimeIdAndStatusInAndLockExpirationAfter(showtimeId , LocalDateTime.now());
        Map<UUID , String> reservationStatusMap = activeReservations.stream()
                .collect(Collectors.toMap(SeatReservation::getSeatId, res->res.getStatus().name()));

        return physicalSeats.stream().map(seat -> ShowtimeSeatResponse.builder()
                .seatId(seat.getId())
                .rowName(seat.getRowName())
                .seatNumber(seat.getSeatNumber())
                .seatType(seat.getSeatType())
                .status(reservationStatusMap.getOrDefault(seat.getId(), "AVAILABLE"))
                .build())
                .collect(Collectors.toList());

    }
    @Override
    @Transactional
    public List<UUID> lockSeatsForCheckout(LockSeatsRequest request) {

        log.info("Attempting to lock {} seats for showtime {}",
                request.getSeatIds().size(),
                request.getShowtimeId());

        // Verify the showtime exists
        var showtime = showtimeCacheRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> {
                    log.warn("Showtime {} was not found", request.getShowtimeId());
                    return new SeatNotShowTimeException(
                            "Showtime not found with id " + request.getShowtimeId());
                });

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiration = now.plusMinutes(10);

        // Fetch active reservations
        List<SeatReservation> activeReservations =
                seatReservationRepository.findByShowtimeIdAndStatusInAndLockExpirationAfter(
                        request.getShowtimeId(),
                        now
                );

        Set<UUID> unavailableSeatIds = activeReservations.stream()
                .map(SeatReservation::getSeatId)
                .collect(Collectors.toSet());

        List<UUID> lockedSeatIds = new ArrayList<>();

        for (UUID seatId : request.getSeatIds()) {

            // Verify seat exists
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> {
                        log.warn("Seat {} was not found", seatId);
                        return new SeatNotFoundException(
                                "Seat not found with id " + seatId);
                    });

            // Verify seat belongs to the same hall as the showtime
            if (!seat.getHallId().equals(showtime.getHallId())) {
                log.warn("Seat {} does not belong to hall {}", seatId, showtime.getHallId());
                throw new SeatNotHallException(
                        "Seat " + seatId + " does not belong to the hall of this showtime."
                );
            }

            // Verify seat isn't already locked/booked
            if (unavailableSeatIds.contains(seatId)) {
                log.warn("Seat {} is unavailable", seatId);
                throw new SeatUnavailableException(
                        "Seat " + seatId + " is already locked or booked."
                );
            }

            SeatReservation reservation = SeatReservation.builder()
                    .showtimeId(request.getShowtimeId())
                    .seatId(seatId)
                    .userId(request.getUserId())
                    .status(ReservationStatus.LOCKED)
                    .lockExpiration(expiration)
                    .build();

            seatReservationRepository.save(reservation);

            lockedSeatIds.add(seatId);
        }

        log.info("Successfully locked {} seats", lockedSeatIds.size());

        return lockedSeatIds;
    }


    @Override
    @Transactional
    public void bookSeats(LockSeatsRequest request) {

        List<SeatReservation> reservations = new ArrayList<>();

        for(UUID id : request.getSeatIds()){

            SeatReservation seat = seatReservationRepository
                    .findLockedReservation(id, request.getShowtimeId())
                    .orElseThrow(() -> {
                        log.warn("Locked reservation not found for seat {}", id);
                        return new SeatUnavailableException(
                                "Seat " + id + " is not locked."
                        );
                    });
            if(!seat.getUserId().equals(request.getUserId())){
                log.warn("the user who is trying to book you are not the one who locked the seat");
                throw new SeatUnavailableException(
                        "You do not own the lock for seat " + id
                );
            }

            if(seat.getLockExpiration().isBefore(LocalDateTime.now())){
                log.warn("you locked the seat but now it is expired  please retry again");

                throw new SeatUnavailableException("your seat has expired");
            }



            seat.setStatus(ReservationStatus.BOOKED);
            seat.setLockExpiration(null);
            reservations.add(seat);


        }
        seatReservationRepository.saveAll(reservations);



    }



    private SeatResponse convertToResponse(Seat seat) {
        return new SeatResponse(
                seat.getId(),
                seat.getHallId(),
                seat.getRowName(),
                seat.getSeatNumber(),
                seat.getSeatType()

        );
    }

}
