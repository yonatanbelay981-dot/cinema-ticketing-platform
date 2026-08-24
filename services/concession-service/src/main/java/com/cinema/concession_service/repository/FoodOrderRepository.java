package com.cinema.concession_service.repository;


import com.cinema.concession_service.entity.FoodOrder;
import com.cinema.concession_service.entity.FoodOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FoodOrderRepository
        extends JpaRepository<FoodOrder, UUID> {

    List<FoodOrder> findByBookingId(UUID bookingId);

    List<FoodOrder> findByUserId(UUID userId);

    List<FoodOrder> findByStatus(FoodOrderStatus status);
}
