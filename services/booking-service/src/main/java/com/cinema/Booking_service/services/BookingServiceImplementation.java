package com.cinema.Booking_service.services;

import com.cinema.Booking_service.dto.BookingResponse;
import com.cinema.Booking_service.dto.CreateBookingRequest;
import com.cinema.Booking_service.entity.Booking;
import com.cinema.Booking_service.entity.BookingSeat;
import com.cinema.Booking_service.entity.BookingStatus;
import com.cinema.Booking_service.event.BookingCancelledEvent;
import com.cinema.Booking_service.event.ReleaseSeatRequestedEvent;
import com.cinema.Booking_service.event.ShowtimePriceRequestedEvent;
import com.cinema.Booking_service.exception.BookingNotFoundException;
import com.cinema.Booking_service.repository.BookingRepository;
import com.cinema.Booking_service.repository.BookingSeatRepository;
import com.cinema.common_lib.event.BookingAnalyticsStatus;
import com.cinema.common_lib.event.BookingStatusAnalyticsEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class BookingServiceImplementation implements BookingService{

    private final BookingRepository bookingRepository;
    private final  KafkaBookingProducer kafkaBookingProducer;
    private final BookingSeatRepository bookingSeatRepository;

    public BookingServiceImplementation(BookingRepository bookingRepository, KafkaBookingProducer kafkaBookingProducer, BookingSeatRepository bookingSeatRepository) {
        this.bookingRepository = bookingRepository;
        this.kafkaBookingProducer = kafkaBookingProducer;
        this.bookingSeatRepository = bookingSeatRepository;
    }

    @Override
    public Page<BookingResponse> getAllBooking(Pageable pageable) {
        log.info("Fetching all bookings with pagination: page number = {}, page size = {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Booking> books  = bookingRepository.findAll(pageable);
        log.info("Fetched {} bookings", books.getNumberOfElements());
        return books.map(this::mapToBookingResponse);
    }

    @Override
    public BookingResponse getBookingById(UUID id ,  String keycloakUserId) {
        log.info("Fetching booking with Id:{}" , id);
        Booking booking = bookingRepository.findByIdAndKeycloakUserId(id, keycloakUserId).orElseThrow(() -> {
            log.warn("Booking not found with Id:{} and userId:{}", id, keycloakUserId);
            return new BookingNotFoundException("Booking not found");
        });

        return  mapToBookingResponse(booking);
    }
    @Override
    public BookingResponse createBooking(CreateBookingRequest request , String keycloakUserId) {

        log.info("Creating booking with request: {}", request);

        Booking booking = new Booking();


        booking.setKeycloakUserId(keycloakUserId);
        booking.setShowtimeId(request.getShowtimeId());
        booking.setPaymentMethod(request.getPaymentMethod());
        booking.setPromotionCode(request.getPromotionCode());
        booking.setStatus(BookingStatus.PENDING);
        booking.setTotalPrice(BigDecimal.ZERO);

        Booking savedBooking = bookingRepository.save(booking);




        List<BookingSeat> bookingSeats = new ArrayList<>();

        for (UUID seatId : request.getSeatIds()) {

            BookingSeat bookingSeat = new BookingSeat();

            bookingSeat.setBooking(savedBooking);
            bookingSeat.setSeatId(seatId);
            bookingSeat.setPrice(BigDecimal.ZERO);

            bookingSeats.add(bookingSeat);
        }
        bookingSeatRepository.saveAll(bookingSeats);

        log.info(
                "Booking created successfully with Id: {}",
                savedBooking.getId()
        );

        // Ask Showtime Service for the price
        kafkaBookingProducer.publishShowtimePriceRequest(
                savedBooking.getId(),
                new ShowtimePriceRequestedEvent(
                        savedBooking.getId(),
                        savedBooking.getShowtimeId()
                )
        );

        BookingResponse response = mapToBookingResponse(savedBooking);
        response.setSeatIds(request.getSeatIds());

        return response;
    }
    @Override
    public void deleteBookingById(UUID id) {
        log.info("Deleting booking with Id:{}", id);
        Booking book = bookingRepository.findById(id).orElseThrow(()->{
            log.warn("while deleting Booking not found with Id:{}", id);
            return  new BookingNotFoundException("Booking not found");
        });
        bookingRepository.delete(book);
        log.info("Booking deleted successfully with Id:{}", id);
    }


    @Override
    public Page<BookingResponse> searchBookingByUserId(String keycloakUserId , Pageable pageable) {
        log.info("Searching bookings by userId: {} with pagination: page number = {}, page size = {}", keycloakUserId, pageable.getPageNumber(), pageable.getPageSize());
        Page<Booking> bookings = bookingRepository.findByKeycloakUserId(keycloakUserId, pageable);
        log.info("Found {} bookings for userId: {}", bookings.getNumberOfElements(), keycloakUserId);
        return bookings.map(this::mapToBookingResponse);
    }

    @Override
    public Page<BookingResponse> searchByShowTimeId(UUID showTimeId, Pageable pageable) {
        log.info("searching  bookings by showtimeId {} with pagination :  page number {} , pageSIze = {} " , showTimeId , pageable.getPageNumber() , pageable.getPageSize());
        Page<Booking> bookings = bookingRepository.findByShowtimeId(showTimeId , pageable);
        log.info("Found {} bookings for showtimeId: {}", bookings.getNumberOfElements(), showTimeId);
        return bookings.map(this::mapToBookingResponse);
    }

    @Override
    public Page<BookingResponse> searchByStatus(BookingStatus status, Pageable pageable) {
        log.info("Searching bookings by status: {} with pagination: page number = {}, page size = {}", status, pageable.getPageNumber(), pageable.getPageSize());
        Page<Booking> bookings = bookingRepository.findByStatus(status, pageable);
        log.info("Found {} bookings for status: {}", bookings.getNumberOfElements(), status);
        return bookings.map(this::mapToBookingResponse);
    }

    @Override
    public Page<BookingResponse> searchByUserIdAndStatus(String keycloakUserId , BookingStatus status, Pageable pageable) {
        log.info("Searching bookings by userId: {} and status: {} with pagination: page number = {}, page size = {}", keycloakUserId, status, pageable.getPageNumber(), pageable.getPageSize());
        Page<Booking> bookings = bookingRepository.findByKeycloakUserIdAndStatus(keycloakUserId, status, pageable);
        log.info("Found {} bookings for userId: {} and status: {}", bookings.getNumberOfElements(), keycloakUserId, status);
        return bookings.map(this::mapToBookingResponse);
    }

    @Override
    public Optional<BookingResponse> getByIdAndUserId(UUID id, String keycloakUserId) {
        log.info("Searching booking by id: {} and userId: {}", id, keycloakUserId);
        Optional<Booking> booking = bookingRepository.findByIdAndKeycloakUserId(id, keycloakUserId);
        if (booking.isPresent()) {
            log.info("Booking found with id: {} and userId: {}", id, keycloakUserId);
            return booking.map(this::mapToBookingResponse);
        } else {
            log.warn("Booking not found with id: {} and userId: {}", id, keycloakUserId);
            return Optional.empty();
        }
    }

    @Override
    public void cancelBooking(UUID bookingId, String keycloakUserId) {

        log.info(
                "Cancelling booking with Id: {}",
                bookingId
        );

        Booking booking =
                bookingRepository.findByIdAndKeycloakUserId(bookingId, keycloakUserId)
                        .orElseThrow(() -> {

                            log.warn(
                                    "while trying to cancel Booking not found with Id: {}",
                                    bookingId
                            );

                            return new BookingNotFoundException(
                                    "Booking not found"
                            );
                        });

        if (booking.getStatus() != BookingStatus.LOCKED
                && booking.getStatus() != BookingStatus.CONFIRMED) {

            throw new IllegalStateException(
                    "Booking cannot be cancelled in its current status: "
                            + booking.getStatus()
            );
        }

        // Change booking status
        booking.setStatus(BookingStatus.CANCELLED);

        Booking savedBooking =
                bookingRepository.save(booking);

        log.info(
                "Booking {} changed to CANCELLED",
                savedBooking.getId()
        );


        BookingCancelledEvent cancelledEvent =
                new BookingCancelledEvent(
                        UUID.randomUUID(),
                        savedBooking.getId(),
                        savedBooking.getKeycloakUserId()
                );

        CompletableFuture<SendResult<String, Object>> cancellationFuture =
                kafkaBookingProducer.publish(
                        savedBooking.getId(),
                        cancelledEvent
                );

        cancellationFuture.thenAccept(result ->
                log.info(
                        "BOOKING_CANCELLED published successfully for bookingId: {}",
                        savedBooking.getId()
                )
        ).exceptionally(ex -> {

            log.error(
                    "Failed to publish BOOKING_CANCELLED for bookingId: {}",
                    savedBooking.getId(),
                    ex
            );

            return null;
        });


        ReleaseSeatRequestedEvent releaseSeatEvent =
                new ReleaseSeatRequestedEvent(
                        ReleaseSeatRequestedEvent.EventType.RELEASE_SEAT_REQUESTED,
                        savedBooking.getId(),
                        savedBooking.getShowtimeId(),
                        savedBooking.getBookingSeats()
                                .stream()
                                .map(BookingSeat::getSeatId)
                                .toList(),
                        savedBooking.getKeycloakUserId()
                );

        CompletableFuture<SendResult<String, Object>> releaseFuture =
                kafkaBookingProducer.publish(
                        savedBooking.getId(),
                        releaseSeatEvent
                );

        releaseFuture.thenAccept(result ->
                log.info(
                        "RELEASE_SEAT_REQUESTED published successfully for bookingId: {}",
                        savedBooking.getId()
                )
        ).exceptionally(ex -> {

            log.error(
                    "Failed to publish RELEASE_SEAT_REQUESTED for bookingId: {}",
                    savedBooking.getId(),
                    ex
            );

            return null;
        });

        BookingStatusAnalyticsEvent statusAnalyticsEvent =
                new BookingStatusAnalyticsEvent(
                        UUID.randomUUID(),
                        savedBooking.getId(),
                        savedBooking.getKeycloakUserId(),
                        savedBooking.getShowtimeId(),
                        savedBooking.getMovieId(),
                        BookingAnalyticsStatus.CANCELLED
                );
        CompletableFuture<SendResult<String, Object>> future1 =
                kafkaBookingProducer.publishBookingStatusAndAnalyticsEvent(
                        savedBooking.getId(),
                        statusAnalyticsEvent
                );

        future1.thenAccept(result ->
                log.info(
                        "BOOKING-STATUS_ANALYTICS_EVENT published successfully for bookingId: {}",
                        savedBooking.getId()
                )
        ).exceptionally(ex -> {

            log.error(
                    "Failed to publish BOOKING_ANALYTICS_EVENT for bookingId: {}",
                    savedBooking.getId(),
                    ex
            );

            return null;
        });

    }


    private BookingResponse mapToBookingResponse(Booking booking) {
        BookingResponse response = new BookingResponse();

        response.setId(booking.getId());
        response.setKeycloakUserId(booking.getKeycloakUserId());
        response.setShowtimeId(booking.getShowtimeId());

        response.setSeatIds(
                booking.getBookingSeats()
                        .stream()
                        .map(BookingSeat::getSeatId)
                        .toList()
        );

        response.setTotalPrice(booking.getTotalPrice());
        response.setStatus(booking.getStatus());
        response.setCreatedAt(booking.getCreatedAt());
        response.setUpdatedAt(booking.getUpdatedAt());

        return response;
    }
}
