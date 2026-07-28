package com.cinema.cinema_service.dto;

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
   @NotBlank(message = "name of hall can not be empty")
   private  String name;
   @NotBlank(message = "capacity of a hall can not be empty")
   private  Integer capacity;
   @NotNull(message = "cinemaId is required")
   private UUID cinemaId;
}
