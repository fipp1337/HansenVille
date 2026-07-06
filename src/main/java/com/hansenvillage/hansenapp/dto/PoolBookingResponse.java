package com.hansenvillage.hansenapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PoolBookingResponse {
   private UUID bookingId;
   private UUID poolSessionId;
   private UUID userId;
   private String userName;
   private Integer userAge;
}
