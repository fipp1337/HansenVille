package com.hansenvillage.hansenapp.dto;

import com.hansenvillage.hansenapp.entity.SessionStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
public class CinemaSessionSlotRequest {

    @NotBlank
    private String movieName;

    @NotNull
    private LocalDateTime startAt;

    @NotNull
    private int duration;

    @NotNull
    private String description;

    @NotNull
    private UUID hallId;
}