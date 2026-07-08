package com.hansenvillage.hansenapp.dto;

import lombok.Data;

import java.time.LocalTime;
import java.util.UUID;

@Data
public class PoolTemplateResponse {
    private UUID id;
    private int dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private int maxCapacity;
}