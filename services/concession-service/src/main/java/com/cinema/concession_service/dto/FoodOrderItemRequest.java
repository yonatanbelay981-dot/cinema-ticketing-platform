package com.cinema.concession_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record FoodOrderItemRequest(

        @NotNull
        UUID foodItemId,

        @NotNull
        @Min(1)
        Integer quantity

) {
}