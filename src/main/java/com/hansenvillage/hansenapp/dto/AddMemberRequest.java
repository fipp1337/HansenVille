package com.hansenvillage.hansenapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddMemberRequest {

    @NotBlank
    private String fullName;
}