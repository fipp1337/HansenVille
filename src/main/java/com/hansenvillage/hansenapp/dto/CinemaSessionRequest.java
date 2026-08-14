package com.hansenvillage.hansenapp.dto;

import com.hansenvillage.hansenapp.entity.CinemaSessionGenre;
import com.hansenvillage.hansenapp.entity.CinemaSessionType;
import com.hansenvillage.hansenapp.entity.SessionStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

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
    private Integer year;

    @NotBlank
    private String ageRating;

    @NotBlank
    private String description;

    @Enumerated(EnumType.STRING)
    private List<CinemaSessionGenre> genre;

    @Enumerated(EnumType.STRING)
    private CinemaSessionType type;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;
}
