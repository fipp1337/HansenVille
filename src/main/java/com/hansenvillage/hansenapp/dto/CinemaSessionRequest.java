package com.hansenvillage.hansenapp.dto;

import com.hansenvillage.hansenapp.entity.SessionStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@RequiredArgsConstructor
public class CinemaSessionRequest {

    @NotBlank
    private String movieName;

    @NotNull
    private LocalDateTime startAt;

    @NotNull
    private int duration;

    @NotNull
    private String description;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

}
