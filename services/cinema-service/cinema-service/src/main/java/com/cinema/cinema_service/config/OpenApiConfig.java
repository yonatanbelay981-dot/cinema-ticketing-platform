package com.cinema.cinema_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

public class OpenApiConfig {
    public OpenAPI OpenApiConfigHandler(){
        return new OpenAPI().info(
                new Info().title("Cinema Ticketing Movie Service API")
                        .version("1.0")
                        .description("API documentation for Cinema Service")

        );
    }

}
