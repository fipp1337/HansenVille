package com.hansenvillage.hansenapp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CinemaPublishWeekScheduleRequest {

    @NotEmpty
    @Valid
    private List<CinemaDaysScheduleRequest> days;
}
