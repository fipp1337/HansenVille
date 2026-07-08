package com.hansenvillage.hansenapp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class PoolTemplateRequest {
    @NotNull
    private int dayOfWeek;

    private int maxCapacity = 40;

    @NotEmpty
    @Valid
    private List<TimeSlot> slots;

    @Data
    public static class TimeSlot {
        @NotNull
        private LocalTime startTime;

        @NotNull
        private LocalTime endTime;
    }
}
