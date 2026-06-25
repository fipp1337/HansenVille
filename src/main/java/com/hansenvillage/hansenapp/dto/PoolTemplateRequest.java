package com.hansenvillage.hansenapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class PoolTemplateRequest {

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @NotNull
    private int dayOfWeek;

    private int maxCapacity = 40;
}
