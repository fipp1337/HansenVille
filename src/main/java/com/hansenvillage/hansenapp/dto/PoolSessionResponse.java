package com.hansenvillage.hansenapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoolSessionResponse {
    private UUID id;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer maxCapacity;
    private Integer bookedCount;
    private String status;
}
