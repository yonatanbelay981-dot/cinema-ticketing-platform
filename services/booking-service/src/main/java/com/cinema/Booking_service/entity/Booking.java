package com.cinema.Booking_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings" ,
       indexes = {
        @Index(name = "idx_booking_showtime_id", columnList = "showtimeId"),
        @Index(name = "idx_booking_user_id", columnList = "keycloakUserId")
})

public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private UUID showtimeId;
    private UUID movieId;
    @Column(nullable = false)
    private String keycloakUserId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;
    @Column(nullable = false)

    private BigDecimal totalPrice;
    private String promotionCode;

    @Column(nullable = false)
    private String paymentMethod;

    @OneToMany(mappedBy = "booking" ,  cascade = CascadeType.ALL)
    private List<BookingSeat> bookingSeats =  new ArrayList<>();
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
