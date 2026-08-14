package com.hansenvillage.hansenapp.dto;

import com.hansenvillage.hansenapp.entity.CinemaSeat;
import com.hansenvillage.hansenapp.entity.CinemaSessionGenre;
import com.hansenvillage.hansenapp.entity.CinemaSessionType;
import com.hansenvillage.hansenapp.entity.SessionStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CinemaSessionResponse {

    private UUID id;
    private String movieName;
    private LocalDateTime startAt;
    private Integer duration;
    private Integer year;
    private String ageRating;
    private List<CinemaSessionGenre> genre;
    private String description;
    private CinemaSessionType type;
    private String posterUrl;
    @Enumerated(EnumType.STRING)
    private SessionStatus status;
}