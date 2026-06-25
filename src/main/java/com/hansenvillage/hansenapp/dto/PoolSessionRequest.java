package com.hansenvillage.hansenapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class PoolSessionRequest {

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    private Integer maxCapacity;

    private String status;

    private LocalDate sessionDate;

    private int dayOfWeek;

}
