package com.hansenvillage.hansenapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class CinemaSessionSlotRequest {

    @NotBlank
    private String movieName;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private int duration;

    @NotNull
    private Integer maxCapacity;
}