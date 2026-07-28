package com.cinema.cinema_service.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HallResponse {

    private UUID id;
    private String name;
    private Integer capacity;
    private UUID cinemaId;



}
