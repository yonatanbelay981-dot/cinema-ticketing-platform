package com.cinema.cinema_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateHallRequest {

   @NotBlank(message = "name of hall cannot be empty")
   private String name;

   @NotNull(message = "capacity of a hall is required")
   private Integer capacity;

   @NotNull(message = "cinemaId is required")
   private UUID cinemaId;
}
