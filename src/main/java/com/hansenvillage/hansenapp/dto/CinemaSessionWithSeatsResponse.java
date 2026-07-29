package com.hansenvillage.hansenapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CinemaSessionWithSeatsResponse {

    private UUID id;
    private String movieName;
    private LocalDateTime startAt;
    private Integer duration;
    private String description;
    private String posterImage;

    private List<CinemaSeatWithAvailableResponse> seats;
}
