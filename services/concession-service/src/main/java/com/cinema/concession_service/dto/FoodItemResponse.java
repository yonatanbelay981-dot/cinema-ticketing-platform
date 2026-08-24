package com.cinema.concession_service.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record FoodItemResponse(

        UUID id,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        Boolean available,
        LocalDateTime createdAt

) {
}
