package com.hansenvillage.hansenapp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "pool_sessions")
public class PoolSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalTime startTime;

    private LocalTime endTime;

    private int maxCapacity = 40;

    private String status;

    private LocalDate sessionDate;

    private int bookedCount = 0;

    private int dayOfWeek;

}