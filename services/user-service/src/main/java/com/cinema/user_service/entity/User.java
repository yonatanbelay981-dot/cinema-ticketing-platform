package com.cinema.user_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(
        name = "users" ,
        uniqueConstraints  = {
                @UniqueConstraint(name = "uniqueEmail"  , columnNames = "email")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @SequenceGenerator(

            name = "user_sequence"  ,
            sequenceName = "user_sequence",
            allocationSize = 1

    )
    @GeneratedValue(

                   strategy = GenerationType.SEQUENCE ,

                   generator = "user_sequence"
    )
    private Long id;
    @NotBlank(message = "first name can not be blank or empty")
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "lastname name can not be blank or empty")
    private  String lastName;
    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "Invalid phone number"
    )
    private String phoneNumber;


    @Email(message = "email cannot be empty")
    @NotBlank
    @Column(nullable = false )
    private String email;
    @NotNull(message = "Date of birth is required")

    private LocalDate dateOfBirth;

    private String profileImage;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
