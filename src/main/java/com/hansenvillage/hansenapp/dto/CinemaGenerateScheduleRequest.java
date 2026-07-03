package com.hansenvillage.hansenapp.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class CinemaGenerateScheduleRequest {

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;
}
