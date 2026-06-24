package com.hansenvillage.hansenapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PoolBookingRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long poolSessionId;
}