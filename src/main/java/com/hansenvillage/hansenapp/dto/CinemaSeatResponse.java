package com.hansenvillage.hansenapp.dto;

import com.hansenvillage.hansenapp.entity.CinemaSeatStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CinemaSeatResponse {

    private UUID id;
    private String seatNumber;
    private CinemaSeatStatus status;
}
