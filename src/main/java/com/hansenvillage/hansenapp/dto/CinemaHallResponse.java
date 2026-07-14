package com.hansenvillage.hansenapp.dto;

import com.hansenvillage.hansenapp.entity.CinemaSeat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CinemaHallResponse {

    private UUID hallId;
    private String name;
    private List<CinemaSeatResponse> seats;
}
