package com.hansenvillage.hansenapp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class PoolDaysScheduleRequest {
    @NotNull
    private LocalDate sessionDate;
    @NotEmpty
    @Valid
    private List<PoolSessionSlotsRequest> sessions;
}
