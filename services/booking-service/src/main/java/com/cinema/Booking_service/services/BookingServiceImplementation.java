package com.cinema.Booking_service.services;

import com.cinema.Booking_service.dto.BookingResponse;
import com.cinema.Booking_service.dto.CreateBookingRequest;
import com.cinema.Booking_service.entity.Booking;
import com.cinema.Booking_service.entity.BookingStatus;
import com.cinema.Booking_service.exception.BookingNotFoundException;
import com.cinema.Booking_service.repository.BookingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.util.Optional;
import java.util.UUID;
@Service
@Slf4j
public class BookingServiceImplementation implements BookingService{

    private final BookingRepository bookingRepository;

    public BookingServiceImplementation(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Page<BookingResponse> getAllBooking(Pageable pageable) {
        log.info("Fetching all bookings with pagination: page number = {}, page size = {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Booking> books  = bookingRepository.findAll(pageable);
        log.info("Fetched {} bookings", books.getNumberOfElements());
        return books.map(this::mapToBookingResponse);
    }

    @Override
    public BookingResponse getBookingById(UUID id) {
        log.info("Fetching booking with Id:{}" , id);
        Booking response =  bookingRepository.findById(id).orElseThrow(() -> {
            log.warn("Booking not found with Id:{}", id);
            return new BookingNotFoundException("Booking not found");
        });
        return  mapToBookingResponse(response);
    }

    @Override
    public BookingResponse createBooking(CreateBookingRequest request) {
       log.info("creating booking with request:{}", request);
       Booking booking =  new Booking();
       booking.setUserId(request.getUserId());
       booking.setShowtimeId(request.getShowtimeId());
       booking.setStatus(BookingStatus.PENDING);
       Booking savedBooking = bookingRepository.save(booking);
       log.info("Booking created successfully with Id:{}", savedBooking.getId());
       return mapToBookingResponse(savedBooking);
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
    public Page<BookingResponse> searchBookingByUserId(UUID userId, Pageable pageable) {
        log.info("Searching bookings by userId: {} with pagination: page number = {}, page size = {}", userId, pageable.getPageNumber(), pageable.getPageSize());
        Page<Booking> bookings = bookingRepository.findByUserId(userId, pageable);
        log.info("Found {} bookings for userId: {}", bookings.getNumberOfElements(), userId);
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
    public Page<BookingResponse> searchByUserIdAndStatus(UUID user_Id, BookingStatus status, Pageable pageable) {
        log.info("Searching bookings by userId: {} and status: {} with pagination: page number = {}, page size = {}", user_Id, status, pageable.getPageNumber(), pageable.getPageSize());
        Page<Booking> bookings = bookingRepository.findByUserIdAndStatus(user_Id, status, pageable);
        log.info("Found {} bookings for userId: {} and status: {}", bookings.getNumberOfElements(), user_Id, status);
        return bookings.map(this::mapToBookingResponse);
    }

    @Override
    public Optional<BookingResponse> getByIdAndUserId(UUID id, UUID user_id) {
        log.info("Searching booking by id: {} and userId: {}", id, user_id);
        Optional<Booking> booking = bookingRepository.findByIdAndUserId(id, user_id);
        if (booking.isPresent()) {
            log.info("Booking found with id: {} and userId: {}", id, user_id);
            return booking.map(this::mapToBookingResponse);
        } else {
            log.warn("Booking not found with id: {} and userId: {}", id, user_id);
            return Optional.empty();
        }
    }

    private BookingResponse mapToBookingResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setUserId(booking.getUserId());
        response.setShowtimeId(booking.getShowtimeId());
        response.setTotalPrice(booking.getTotalPrice());
        response.setStatus(booking.getStatus());
        response.setCreatedAt(booking.getCreatedAt());
        response.setUpdatedAt(booking.getUpdatedAt());
        return response;
    }
}
