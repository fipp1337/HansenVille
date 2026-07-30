package com.hansenvillage.hansenapp.dto;

import com.hansenvillage.hansenapp.entity.OtpType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResendCodeRequest(
        @NotBlank @Email String email,
        @NotNull OtpType type
) {}
