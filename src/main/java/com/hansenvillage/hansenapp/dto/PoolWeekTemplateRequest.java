package com.hansenvillage.hansenapp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class PoolWeekTemplateRequest {

    @NotEmpty
    @Valid
    private List<PoolTemplateRequest> days;
}