package com.hansenvillage.hansenapp.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class CinemaBookingRequest {

    @NotNull
    private UUID userId;

    @NotNull
    private UUID cinemaSessionId;

    @NotEmpty
    private List<UUID> seatIds;
}