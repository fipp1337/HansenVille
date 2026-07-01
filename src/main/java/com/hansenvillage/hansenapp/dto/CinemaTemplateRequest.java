package com.hansenvillage.hansenapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@RequiredArgsConstructor
public class CinemaTemplateRequest {

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @NotNull
    private int dayOfWeek;

    private int maxCapacity = 15;
}
