package com.hansenvillage.hansenapp.dto;

import com.hansenvillage.hansenapp.entity.CinemaSeat;
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

    private String posterUrl;
}