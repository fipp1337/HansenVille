package com.hansenvillage.hansenapp.dto;

import com.hansenvillage.hansenapp.entity.PoolBookingStatus;
import com.hansenvillage.hansenapp.entity.SessionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
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
   private PoolBookingStatus status;
   private LocalDate sessionDate;

   @NotNull
   private LocalTime startTime;

   @NotNull
   private LocalTime endTime;
}
