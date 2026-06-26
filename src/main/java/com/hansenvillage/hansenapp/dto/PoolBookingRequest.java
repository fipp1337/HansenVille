package com.hansenvillage.hansenapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class PoolBookingRequest {

    @NotNull
    private UUID userId;

    @NotNull
    private UUID poolSessionId;
}