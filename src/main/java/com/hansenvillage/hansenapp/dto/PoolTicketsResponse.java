package com.hansenvillage.hansenapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class PoolTicketsResponse {
    private long maxTickets;
    private long remainingTickets;
    private LocalDateTime resetTime;
}
