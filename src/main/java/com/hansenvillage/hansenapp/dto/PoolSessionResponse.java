package com.hansenvillage.hansenapp.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class PoolSessionResponse {
    private UUID id;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer maxCapacity;
    private Integer bookedCount;
    private String status;
}
