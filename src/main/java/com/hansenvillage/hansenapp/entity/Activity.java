package com.hansenvillage.hansenapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "activities")
@Data
public class Activity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 150)
    private String description;

    private String image;
}