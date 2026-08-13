package com.hansenvillage.hansenapp.dto;

import com.hansenvillage.hansenapp.entity.CinemaSeatStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class CinemaSeatWithAvailableResponse {

    private UUID id;
    private String seatNumber;
//    private boolean available;
    private CinemaSeatStatus status;
}
