package com.hansenvillage.hansenapp.dto;

import com.hansenvillage.hansenapp.entity.SessionStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    private LocalDate sessionDate;
}
