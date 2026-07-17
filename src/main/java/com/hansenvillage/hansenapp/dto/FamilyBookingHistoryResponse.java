package com.hansenvillage.hansenapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyBookingHistoryResponse {
    private List<PoolBookingResponse> poolBookings;
    private List<CinemaBookingResponse> cinemaBookings;
}