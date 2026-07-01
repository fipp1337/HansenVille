package com.hansenvillage.hansenapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddMemberRequest {

    @NotBlank
    private String name;

    @NotNull
    @Min(value = 0)
    private Integer age;
}