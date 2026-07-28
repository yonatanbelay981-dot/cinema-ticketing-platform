package com.cinema.cinema_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cinema" ,
        indexes = {
                @Index(name = "idx_cinema_name", columnList = "name"),
                @Index(name = "idx_cinema_address", columnList = "address"),
                @Index(name = "idx_cinema_phone", columnList = "phone")
        }


)

public class Cinema {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phone;

    @OneToMany(mappedBy = "cinema")
    private List<Hall> hall;

}
