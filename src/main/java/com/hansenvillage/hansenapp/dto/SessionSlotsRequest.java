package com.hansenvillage.hansenapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
@Getter
@Setter
public class SessionSlotsRequest {
    @NotNull
    private LocalTime startTime;
    @NotNull
    private LocalTime endTime;
    @Min(value = 1)
    private int maxCapacity;
}
