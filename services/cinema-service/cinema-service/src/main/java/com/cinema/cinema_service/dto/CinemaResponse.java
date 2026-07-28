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
public class CinemaResponse {

            private UUID id;
            private String name;
            private String address;
            private String phone;

}
