package com.cinema.concession_service.dto;




import com.cinema.concession_service.entity.FoodOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record FoodOrderResponse(

        UUID id,

        UUID bookingId,

        UUID userId,

        BigDecimal totalPrice,

        FoodOrderStatus status,

        List<FoodOrderItemResponse> items,

        LocalDateTime createdAt

) {
}
