package com.hansenvillage.hansenapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CinemaBookingResponse {
    private UUID bookingId;
    private UUID cinemaSessionId;
    private UUID userId;
}