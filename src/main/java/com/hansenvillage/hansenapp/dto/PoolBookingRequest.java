package com.hansenvillage.hansenapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PoolBookingRequest {

    @NotNull
    private List<UUID> userIds;

    @NotNull
    private UUID poolSessionId;
}