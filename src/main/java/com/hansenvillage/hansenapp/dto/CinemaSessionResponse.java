package com.hansenvillage.hansenapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CinemaSessionResponse {
    private UUID id;
    private String movieName;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private Integer duration;
    private Integer maxCapacity;
    private Integer bookedCount;
}