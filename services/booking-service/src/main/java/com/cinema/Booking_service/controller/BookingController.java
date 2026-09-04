package com.cinema.Booking_service.controller;
import com.cinema.Booking_service.dto.BookingResponse;
import com.cinema.Booking_service.dto.CreateBookingRequest;
import com.cinema.Booking_service.entity.BookingStatus;
import com.cinema.Booking_service.services.BookingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/me")
    public ResponseEntity<Page<BookingResponse>> getMyBookings(
            Pageable pageable,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String keycloakUserId = jwt.getSubject();

        return ResponseEntity.ok(
                bookingService.searchBookingByUserId(keycloakUserId, pageable)
        );
    }



    @GetMapping("/admin")
    public ResponseEntity<Page<BookingResponse>> getAllBookings(
            Pageable pageable
    ) {

        Page<BookingResponse> bookings =
                bookingService.getAllBooking(pageable);

        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(
            @PathVariable UUID id ,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String keycloakUserId = jwt.getSubject();
        BookingResponse booking =
                bookingService.getBookingById(id ,  keycloakUserId);

        return ResponseEntity.ok(booking);
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody CreateBookingRequest request , @AuthenticationPrincipal Jwt jwt
            ) {
        String keycloakUserId = jwt.getSubject();
        BookingResponse booking =
                bookingService.createBooking(request  , keycloakUserId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(booking);
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteBooking(
            @PathVariable UUID id
    ) {

        bookingService.deleteBookingById(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/user/{keycloakUserId}")
    public ResponseEntity<Page<BookingResponse>> getBookingsByUserId(
            @PathVariable String keycloakUserId,
            Pageable pageable
    ) {

        Page<BookingResponse> bookings =
                bookingService.searchBookingByUserId(
                        keycloakUserId,
                        pageable
                );

        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/admin/showtime/{showtimeId}")
    public ResponseEntity<Page<BookingResponse>> getBookingsByShowtimeId(
            @PathVariable UUID showtimeId,
            Pageable pageable
    ) {

        Page<BookingResponse> bookings =
                bookingService.searchByShowTimeId(
                        showtimeId,
                        pageable
                );

        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/admin/status/{status}")
    public ResponseEntity<Page<BookingResponse>> getBookingsByStatus(
            @PathVariable BookingStatus status,
            Pageable pageable
    ) {

        Page<BookingResponse> bookings =
                bookingService.searchByStatus(
                        status,
                        pageable
                );

        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/admin/user/{keycloakUserId}/status/{status}")
    public ResponseEntity<Page<BookingResponse>> getBookingsByUserIdAndStatus(
            @PathVariable String keycloakUserId,
            @PathVariable BookingStatus status,
            Pageable pageable
    ) {

        Page<BookingResponse> bookings =
                bookingService.searchByUserIdAndStatus(
                        keycloakUserId,
                        status,
                        pageable
                );

        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/admin/{id}/user/{keycloakUserId}")
    public ResponseEntity<BookingResponse> getBookingByIdAndUserId(
            @PathVariable UUID id,
            @PathVariable String keycloakUserId
    ) {

        return bookingService
                .getByIdAndUserId(id, keycloakUserId)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable UUID id ,
            @AuthenticationPrincipal Jwt jwt
    ) {
      String keycloakUserId = jwt.getSubject();
        bookingService.cancelBooking(id, keycloakUserId);

        return ResponseEntity.noContent().build();
    }
}


