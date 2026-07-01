package com.hansenvillage.hansenapp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "pool_sessions")
public class PoolSession {

    @Id
    @GeneratedValue
    private UUID id;

    private LocalTime startTime;

    private LocalTime endTime;

    private Integer maxCapacity;

    @Enumerated(EnumType.STRING)
    private PoolSessionStatus status;

    private LocalDate sessionDate;

    private int bookedCount = 0;

    @Version
    private Integer version;
}