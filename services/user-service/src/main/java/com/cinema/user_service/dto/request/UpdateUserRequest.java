package com.cinema.user_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    @NotBlank(message = "first-name name can not be blank or empty")
    private String firstName;
    @NotBlank(message = "last-name name can not be blank or empty")
    private  String lastName;
    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "Invalid phone number"
    )
    private String phoneNumber;


    @Email(message = "invalid email format ")
    @NotBlank(message = "Emial is required")
    private String email;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;
    private String profileImage;
}
