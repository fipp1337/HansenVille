package com.hansenvillage.hansenapp.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class CinemaSeatWithAvailableResponse {

    private UUID id;
    private String sofaNumber;
    private boolean available;
}
