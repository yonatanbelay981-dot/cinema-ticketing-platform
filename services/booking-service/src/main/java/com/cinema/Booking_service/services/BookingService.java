package com.cinema.Booking_service.services;

import com.cinema.Booking_service.dto.BookingResponse;
import com.cinema.Booking_service.dto.CreateBookingRequest;
import com.cinema.Booking_service.entity.Booking;
import com.cinema.Booking_service.entity.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface BookingService {
    Page<BookingResponse>getAllBooking(Pageable pageable);
    BookingResponse getBookingById(UUID id);
    BookingResponse createBooking(CreateBookingRequest request);
    void deleteBookingById(UUID id);
    Page<BookingResponse>searchBookingByUserId(UUID userId , Pageable pageable);
    Page<BookingResponse>searchByShowTimeId(UUID showTimeId  , Pageable pageable);
    Page<BookingResponse>searchByStatus(BookingStatus status  , Pageable pageable);
    Page<BookingResponse>searchByUserIdAndStatus(UUID user_Id  , BookingStatus status  , Pageable pageable );
    Optional<BookingResponse> getByIdAndUserId(UUID id , UUID user_id);

}
