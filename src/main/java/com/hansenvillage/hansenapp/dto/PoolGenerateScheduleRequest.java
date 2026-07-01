package com.hansenvillage.hansenapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PoolGenerateScheduleRequest {
    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;
}