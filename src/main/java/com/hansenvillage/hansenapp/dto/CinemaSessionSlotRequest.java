package com.hansenvillage.hansenapp.dto;

import com.hansenvillage.hansenapp.entity.SessionStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
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

    private Integer maxCapacity;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    private LocalDate sessionDate;
}