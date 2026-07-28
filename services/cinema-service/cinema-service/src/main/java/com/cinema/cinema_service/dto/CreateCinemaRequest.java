package com.cinema.cinema_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCinemaRequest {
    @NotBlank(message = "name of cinema is required")
    private String name;
    @NotBlank(message = "address of cinema is required")
    private String address;
    private  String phone;
}
