package com.hansenvillage.hansenapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@RequiredArgsConstructor
public class CinemaSessionRequest {

    @NotBlank
    private String movieName;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private int duration;

    private Integer maxCapacity;

    private String status;

    private LocalDate sessionDate;
}
