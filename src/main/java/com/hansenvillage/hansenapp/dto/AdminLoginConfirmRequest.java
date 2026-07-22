package com.hansenvillage.hansenapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminLoginConfirmRequest {
    @NotBlank
    private String email;
    @NotBlank
    private String verificationCode;
}
