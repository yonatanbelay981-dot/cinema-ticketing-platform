package com.cinema.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


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

    @GeneratedValue(

                   strategy = GenerationType.UUID


    )
    private UUID id;
    @Column(nullable = false, unique = true)
    private String keycloakUserId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private  String lastName;

    private String phoneNumber;


    @Column(nullable = false )
    private String email;
    @Column(nullable = false)
    private LocalDate dateOfBirth;

    private String profileImage;

    @CreationTimestamp
    @Column(nullable = false , updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
