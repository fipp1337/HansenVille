package com.hansenvillage.hansenapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "pool_templates")
public class PoolTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    private LocalTime startTime;

    private LocalTime endTime;

    private int dayOfWeek;

    private int maxCapacity = 40;
}
