package com.hansenvillage.hansenapp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Family {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "address", nullable = false, length = 255)
    private String address;
}