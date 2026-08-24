package com.cinema.concession_service.repository;



import com.cinema.concession_service.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FoodItemRepository
        extends JpaRepository<FoodItem, UUID> {
}
