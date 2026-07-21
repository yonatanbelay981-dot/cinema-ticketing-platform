package com.cinema.user_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String email;

    private LocalDate dateOfBirth;

    private String profileImage;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
