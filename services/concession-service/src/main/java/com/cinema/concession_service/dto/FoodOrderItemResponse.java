package com.cinema.concession_service.dto;


import java.math.BigDecimal;
import java.util.UUID;

public record FoodOrderItemResponse(

        UUID foodItemId,

        String foodItemName,

        Integer quantity,

        BigDecimal unitPrice,

        BigDecimal subtotal

) {
}
