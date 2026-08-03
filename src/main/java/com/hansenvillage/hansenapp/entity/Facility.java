package com.hansenvillage.hansenapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "facilities")
@Data
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    private String name;

    @Column(length = 100)
    private String description;

    private String image;

    private String targetRoute;

    private boolean active = true;
}