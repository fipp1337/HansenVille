package com.hansenvillage.hansenapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "pool_templates")
public class PoolTemplate {

    @Id
    @GeneratedValue
    private UUID id;

    private LocalTime startTime;

    private LocalTime endTime;

    private int dayOfWeek;

    private int maxCapacity;
}
