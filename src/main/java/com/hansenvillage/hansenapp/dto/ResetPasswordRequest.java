package com.hansenvillage.hansenapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String email;
    @NotBlank
    private String verificationCode;
    @NotBlank
    @Size(min = 6, max = 25)
    private String newPassword;

    @NotBlank
    private String confirmPassword;
}
