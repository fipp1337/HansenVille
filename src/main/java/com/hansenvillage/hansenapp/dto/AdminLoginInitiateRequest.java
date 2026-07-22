package com.hansenvillage.hansenapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminLoginInitiateRequest {
    @NotBlank
    private String email;
}
