package com.hansenvillage.hansenapp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class PoolPublishWeekScheduleRequest {
    @NotEmpty
    @Valid
    private List<PoolDaysScheduleRequest> days;
}
